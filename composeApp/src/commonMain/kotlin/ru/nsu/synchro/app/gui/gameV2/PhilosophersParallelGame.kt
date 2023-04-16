@file:Suppress("MemberVisibilityCanBePrivate", "UnnecessaryVariable")

package ru.nsu.synchro.app.gui.gameV2

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Semaphore
import ru.nsu.synchro.app.machine.ast.EnvNode
import ru.nsu.synchro.app.machine.ast.ForeignFunctionNode
import ru.nsu.synchro.app.machine.dsl.ParallelProgram
import ru.nsu.synchro.app.machine.runtime.ExecutableProgram
import ru.nsu.synchro.app.machine.runtime.ProgramEnvironment
import ru.nsu.synchro.ast.Number
import ru.nsu.synchro.utils.splitString
import ru.nsu.synchro.ast.Program as AstProgram

class PhilosophersParallelGame(
    parsedProgram: AstProgram,
    private val debug: Boolean = true,
) {
    val environment: Environment
    val program: ParallelProgram
    val philosophers: List<Philosopher>

    private val executableProgram: ExecutableProgram

    init {
        val set = parsedProgram.set ?: error("program don't have set of flows")
        val flows = set.flows

        philosophers = flows.map {  flow ->
            Philosopher(
                maxPosition = when(val data = parsedProgram.context?.map?.get(flow.name)) {
                    is Number -> data.number as? Int
                    else -> null
                } ?: 3,
                name = flow.name
            )
        }

        if (debug) {
            println("> [Program] initialize with philosophers:")
            philosophers.forEach { println(it) }
        }

        environment = Environment(philosophers)
        program = PhilosophersParallelGameTranslator.translateProgram(parsedProgram)
        executableProgram = ExecutableProgram(environment, program, debug = debug)
    }

    suspend fun runProgram() {
        executableProgram.startThreads()
        executableProgram.startExecution()
    }

    class Philosopher(
        val maxPosition: Int = 3,
        val name: String? = null
    ) {
        val currentPosition = MutableStateFlow(0)

        val leftForkTaken = MutableStateFlow(false)
        val rightForkTaken = MutableStateFlow(false)

        val isEaten = MutableStateFlow(false)

        override fun toString(): String {
            return "Филосов (${name ?: "N\\A"}, maxPosition = $maxPosition)"
        }
    }

    class Environment(
        private val philosophers: List<Philosopher>
    ) : ProgramEnvironment {

        val forksSemaphores: List<Semaphore> = philosophers.map { Semaphore(1, 0) }

        override suspend fun provideEnvVariable(node: EnvNode): Any? {
            val nodeName = node.name
            val (threadName, variable) = splitString(nodeName)

            val philosopher = philosophers.firstOrNull { it.name == threadName } ?: return null

            return with(PhilosophersParallelGameTranslator.Commands) {
                when (variable) {
                    FWD_FREE -> philosopher.currentPosition.value < philosopher.maxPosition
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
                        philosopher.leftForkTaken.update { true }
                    }

                    TAKE_R_FORK -> with(forksSemaphores[rightForkIndex]) {
                        acquire()
                        philosopher.rightForkTaken.update { true }
                    }
                    EAT -> println("$threadName начал есть!")

                    FIN_EATING -> philosopher.isEaten.update { true }

                    PUT_L_FORK -> with(forksSemaphores[leftForkIndex]) {
                        philosopher.leftForkTaken.update { false }
                        release()
                    }

                    PUT_R_FORK -> with(forksSemaphores[rightForkIndex]) {
                        philosopher.rightForkTaken.update { false }
                        release()
                    }
                    else -> error("illegal action $action")
                }
            }
        }
    }
}
