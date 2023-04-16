@file:Suppress("MemberVisibilityCanBePrivate", "UnnecessaryVariable")

package ru.nsu.synchro.app.gui.gameV2

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Semaphore
import ru.nsu.synchro.app.machine.ast.EnvNode
import ru.nsu.synchro.app.machine.ast.ForeignFunctionNode
import ru.nsu.synchro.app.machine.dsl.ParallelProgram
import ru.nsu.synchro.app.machine.runtime.ProgramEnvironment
import ru.nsu.synchro.utils.splitString
import ru.nsu.synchro.ast.Program as AstProgram

class PhilosophersParallelGame(
    private val parsedProgram: AstProgram
) {
    val environment: Environment
    val program: ParallelProgram
    val philosophers: List<Philosopher>

    init {
        val set = parsedProgram.set ?: error("program don't have set of flows")
        val flows = set.flows

        philosophers = flows.map { Philosopher(maxPosition = 3, name = it.name) }
        environment = Environment(philosophers)
        program = PhilosophersParallelGameTranslator.translateProgram(parsedProgram)
    }

    class Philosopher(
        val maxPosition: Int = 3,
        val name: String? = null
    ) {
        val currentPosition = MutableStateFlow(0)

        val leftForkTaken = MutableStateFlow(false)
        val rightForkTaken = MutableStateFlow(false)

        val isEaten = MutableStateFlow(false)
    }

    class Environment(
        private val philosophers: List<Philosopher>
    ) : ProgramEnvironment {

        val forksSemaphores: List<Semaphore> = philosophers.map { Semaphore(1, 0) }

        override suspend fun provideEnvVariable(node: EnvNode): Any? {
            val nodeName = node.name
            val (threadName, variable) = splitString(nodeName)

            val philosopher = philosophers.first { it.name == threadName }

            return with(PhilosophersParallelGameTranslator.Commands) {
                when (variable) {
                    FWD_FREE -> philosopher.currentPosition.value < philosopher.maxPosition
                    BWD_FREE -> philosopher.currentPosition.value > 0
                    else -> null
                }

            }
        }

        override suspend fun callForeignFunction(node: ForeignFunctionNode) {
            val nodeName = node.name
            val (threadName, variable) = splitString(nodeName)

            val philosopher = philosophers.first { it.name == threadName }
            val philosopherIndex = philosophers.indexOf(philosopher)

            val rightForkIndex = (philosopherIndex - 1).takeIf { it > 0 } ?: (philosophers.size - 1)
            val leftForkIndex = philosopherIndex

            with(PhilosophersParallelGameTranslator.Commands) {
                when (variable) {
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
                        philosopher.leftForkTaken.update { true }
                        release()
                    }

                    PUT_R_FORK -> with(forksSemaphores[rightForkIndex]) {
                        philosopher.rightForkTaken.update { true }
                        release()
                    }
                }
            }
        }
    }
}


