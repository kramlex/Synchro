@file:Suppress("FunctionName", "MemberVisibilityCanBePrivate")

package ru.nsu.synchro.app.gui.gameV2

import ru.nsu.synchro.app.machine.dsl.ParallelProgram
import ru.nsu.synchro.app.machine.dsl.ProgramBuilder
import ru.nsu.synchro.app.machine.dsl.program
import ru.nsu.synchro.ast.Action
import ru.nsu.synchro.ast.Command
import ru.nsu.synchro.ast.Condition
import kotlin.time.Duration.Companion.seconds
import ru.nsu.synchro.ast.Program as AstProgram

object PhilosophersParallelGameTranslator {

        object Commands {
            /**
             * forward
             */
            const val FWD = "ШАГ_ВПЕРЕД"
            fun FWD(threadName: String?) = "${threadName.orEmpty()}###$FWD"

            /**
             * forward free
             */
            const val FWD_FREE = "ВПЕРЕДИ_СВОБОДНО"

            fun FWD_FREE(threadName: String?) = "${threadName.orEmpty()}###$FWD_FREE"

            /**
             * take left fork
             */
            const val TAKE_L_FORK = "ВЗЯТЬ_ЛЕВУЮ_ВИЛКУ"
            fun TAKE_L_FORK(threadName: String?) = "${threadName.orEmpty()}###$TAKE_L_FORK"

            /**
             * take right fork
             */
            const val TAKE_R_FORK = "ВЗЯТЬ_ПРАВУЮ_ВИЛКУ"
            fun TAKE_R_FORK(threadName: String?) = "${threadName.orEmpty()}###$TAKE_R_FORK"

            /**
             * eat
             */
            const val EAT = "ЕСТЬ"
            fun EAT(threadName: String?) = "${threadName.orEmpty()}###$EAT"

            /**
             * finished eating
             */
            const val FIN_EATING = "ПОЕЛ"
            fun FIN_EATING(threadName: String?) = "${threadName.orEmpty()}###$FIN_EATING"

            /**
             * put left fork
             */
            const val PUT_L_FORK = "ПОЛОЖИТЬ_ЛЕВУЮ_ВИЛКУ"
            fun PUT_L_FORK(threadName: String?) = "${threadName.orEmpty()}###$PUT_L_FORK"

            /**
             * put right fork
             */
            const val PUT_R_FORK = "ПОЛОЖИТЬ_ПРАВУЮ_ВИЛКУ"
            fun PUT_R_FORK(threadName: String?) = "${threadName.orEmpty()}###$PUT_R_FORK"

            /**
             * backward
             */
            const val BWD = "ШАГ_НАЗАД"
            fun BWD(threadName: String?) = "${threadName.orEmpty()}###$BWD"

            /**
             * backward free
             */
            const val BWD_FREE = "СЗАДИ_СВОБОДНО"
            fun BWD_FREE(threadName: String?) = "${threadName.orEmpty()}###$BWD_FREE"

            /**
             * everyone ate
             */
            const val EVERYONE_ATE = "ВСЕ ПОЕЛИ"


            // all
            val all: List<String> = listOf(
                FWD,
                FWD_FREE,
                TAKE_L_FORK,
                TAKE_R_FORK,
                EAT,
                FIN_EATING,
                PUT_L_FORK,
                PUT_R_FORK,
                BWD,
                BWD_FREE
            )

            fun findAndAddThread(threadName: String?, symbol: String): String {
                return all.first { it == symbol}.let { "${threadName.orEmpty()}###$it" }
            }
        }

        fun translateProgram(astProgram: AstProgram): ParallelProgram {
            val set = astProgram.set ?: error("program don't have set of flows")
            val flows = set.flows

            return program(set.name.orEmpty()) {
                flows.forEach { astFlow ->
                    val threadName = astFlow.name
                    synchronous(threadName) {
                        astFlow.actions.forEach { astAction ->
                            translateAction(
                                threadName = threadName,
                                action = astAction
                            )
                        }
                    }
                }
            }
        }

        fun ProgramBuilder.translateAction(threadName: String?, action: Action) {
            when (action) {
                is Action.Pause -> delay(action.durationSec.seconds)
                is Action.While -> whileLoop(
                    envName = when (val condition = action.condition) {
                        is Condition.NamedCondition -> condition.name.let { symbol ->
                            Commands.findAndAddThread(threadName = threadName, symbol = symbol)
                        }
                    }
                ) { translateAction(threadName, action.action) }
                is Action.Command -> when(val command = action.command) {
                    is Command.Queue -> synchronous(action.name) {
                        command.actions.forEach { translateAction(threadName, it) }
                    }
                    is Command.SymbolCommand -> command.symbol.let { symbol ->
                        Commands.findAndAddThread(threadName = threadName, symbol = symbol)
                    }.invoke()
                    is Command.Together -> parallel(action.name) {
                        command.actions.forEach { translateAction(threadName, it) }
                    }
                    Command.DOWN -> TODO()
                    Command.LEFT -> TODO()
                    Command.PASS -> TODO()
                    Command.RIGHT -> TODO()
                    Command.START -> TODO()
                    Command.STEP -> TODO()
                    Command.STOP -> TODO()
                    Command.UP -> TODO()
                }
                is Action.If -> TODO()
                is Action.Repeat -> TODO()
                is Action.Wait -> TODO()
            }
        }
    }