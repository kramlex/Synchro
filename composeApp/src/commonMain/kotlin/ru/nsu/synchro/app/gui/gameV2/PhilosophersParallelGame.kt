@file:Suppress("MemberVisibilityCanBePrivate", "UnnecessaryVariable")

package ru.nsu.synchro.app.gui.gameV2

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Semaphore
import ru.nsu.synchro.app.machine.ast.EnvNode
import ru.nsu.synchro.app.machine.ast.ForeignFunctionNode
import ru.nsu.synchro.app.machine.dsl.ParallelProgram
import ru.nsu.synchro.app.machine.runtime.Debugger
import ru.nsu.synchro.app.machine.runtime.ExecutableProgram
import ru.nsu.synchro.app.machine.runtime.ProgramEnvironment
import ru.nsu.synchro.ast.Number
import ru.nsu.synchro.utils.splitString
import ru.nsu.synchro.ast.Program as AstProgram

class PhilosophersParallelGame(
    parsedProgram: AstProgram,
    debug: Boolean = true,
) {
    private val gameScope = CoroutineScope(Dispatchers.Default)

    private val forksSemaphores: List<Semaphore>
    private val executableProgram: ExecutableProgram
    private val _busyForks: MutableStateFlow<List<Int>> = MutableStateFlow(mutableListOf())

    val environment: Environment
    val program: ParallelProgram
    val philosophers: List<Philosopher>
    val runningThreads: Map<String, MutableStateFlow<Boolean>>

    val busyForks: StateFlow<List<Int>> = _busyForks.asStateFlow()

    val philosopherPositions: StateFlow<List<Int>>

    val allThreadIsRunning: StateFlow<Boolean>

    private val _stackTrace: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val stackTrace: StateFlow<List<String>> = _stackTrace.asStateFlow()

    val debugger: Debugger get() = executableProgram.debugger

    init {
        val set = parsedProgram.set ?: error("program don't have set of flows")
        val flows = set.flows

        philosophers = flows.map { flow ->
            Philosopher(
                maxPosition = when (val data = parsedProgram.context?.map?.get(flow.name)) {
                    is Number -> data.number as? Int
                    else -> null
                } ?: 3,
                name = flow.name
            )
        }
        forksSemaphores = philosophers.map { Semaphore(1, 0) }

        if (debug) {
            println("> [Game] initialize with philosophers:")
            philosophers.forEach { println(it) }
        }

        environment = Environment(philosophers, forksSemaphores, _busyForks)
        program = PhilosophersParallelGameTranslator.translateProgram(parsedProgram)

        runningThreads = philosophers.mapNotNull { philosopher ->
            val name = philosopher.name ?: return@mapNotNull null
            Pair(name, philosopher.isRunning)
        }.toMap()

        executableProgram = ExecutableProgram(
            environment = environment,
            program = program,
            debug = debug,
            runningThreads = runningThreads.toMutableMap(),
            stackTrace = _stackTrace
        )

        philosopherPositions = combine(philosophers.map { it.currentPosition }) { it.asList() }
            .stateIn(gameScope, SharingStarted.Eagerly, initialValue = emptyList())

        allThreadIsRunning = combine(philosophers.map { it.isRunning }) { array ->
            array.asList().all { it }
        }.stateIn(gameScope, SharingStarted.Eagerly, initialValue = false)
    }

    suspend fun runProgram() {
        executableProgram.startThreads()
        executableProgram.startExecution()
    }

    suspend fun startExecution() {
        executableProgram.startExecution()
    }

    fun runAllThreads() {
        philosophers.forEach {
            it.isRunning.update { true }
        }
    }

    fun stopAllThreads() {
        philosophers.forEach {
            it.isRunning.update { false }
        }
    }

    suspend fun runProgram(threadsName: List<String>) {
        executableProgram.startThread(threadsName)
        executableProgram.startExecution()
    }

    class Philosopher(
        val maxPosition: Int = 3,
        val name: String? = null,
    ) {
        // tread property
        val isRunning: MutableStateFlow<Boolean> = MutableStateFlow(false)

        val currentPosition = MutableStateFlow(0)
        val leftForkTaken = MutableStateFlow(false)
        val rightForkTaken = MutableStateFlow(false)

        val isEaten = MutableStateFlow(false)

        override fun toString(): String {
            return "Филосов (${name ?: "N\\A"}, maxPosition = $maxPosition)"
        }
    }

    class Environment(
        private val philosophers: List<Philosopher>,
        private val forksSemaphores: List<Semaphore>,
        private val busyForks: MutableStateFlow<List<Int>>,
    ) : ProgramEnvironment {

        override suspend fun provideEnvVariable(node: EnvNode): Any? {
            val nodeName = node.name
            val (threadName, variable) = splitString(nodeName)

            val philosopher = philosophers.firstOrNull { it.name == threadName } ?: return null

            return with(PhilosophersParallelGameTranslator.Commands) {
                when (variable) {
                    FWD_FREE -> philosopher.currentPosition.value < philosopher.maxPosition - 1
                    BWD_FREE -> philosopher.currentPosition.value > 0
                    else -> error("illegal variable")
                }
            }
        }

        override suspend fun callForeignFunction(node: ForeignFunctionNode) {
            val nodeName = node.name
            val (threadName, action) = splitString(nodeName)

            val philosopher = philosophers.first { it.name == threadName }
            val philosopherIndex = philosophers.indexOf(philosopher)

            val rightForkIndex = philosopherIndex.dec().takeIf { it >= 0 } ?: (philosophers.lastIndex)
            val leftForkIndex = philosopherIndex

            with(PhilosophersParallelGameTranslator.Commands) {
                when (action) {
                    FWD -> philosopher.currentPosition.update { it.inc() }

                    BWD -> philosopher.currentPosition.update { it.dec() }

                    TAKE_L_FORK -> with(forksSemaphores[leftForkIndex]) {
                        acquire()
                        busyForks.update { list -> list + leftForkIndex }
                        philosopher.leftForkTaken.update { true }
                    }

                    TAKE_R_FORK -> with(forksSemaphores[rightForkIndex]) {
                        acquire()
                        busyForks.update { list -> list + rightForkIndex }
                        philosopher.rightForkTaken.update { true }
                    }

                    EAT -> println("$threadName начал есть!")

                    FIN_EATING -> philosopher.isEaten.update { true }

                    PUT_L_FORK -> with(forksSemaphores[leftForkIndex]) {
                        philosopher.leftForkTaken.update { false }
                        busyForks.update { list -> list - leftForkIndex }
                        release()
                    }

                    PUT_R_FORK -> with(forksSemaphores[rightForkIndex]) {
                        philosopher.rightForkTaken.update { false }
                        busyForks.update { list -> list - rightForkIndex }
                        release()
                    }

                    else -> error("illegal action $action")
                }
            }
        }
    }
}
