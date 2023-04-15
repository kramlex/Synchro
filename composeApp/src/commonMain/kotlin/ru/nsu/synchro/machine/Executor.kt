package ru.nsu.synchro.machine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.nsu.synchro.ast.Action
import ru.nsu.synchro.ast.Command
import ru.nsu.synchro.ast.Condition
import ru.nsu.synchro.phils.Philosopher

internal object Commands {
    enum class Conditions(val key: String) {
        /**
         * КЛЕТКА ВПЕРЕД СВОБОДНА (Cell Forward is Free)
         */
        CFS("КВС"),
        /**
         * КЛЕТКА НАЗАД СВОБОДНА (Cell Backward is Free)
         */
        CBS("КНС"),
        /**
         * ПРАВАЯ ВИЛКА СВОБОДНА (Right Fork is Free)
         */
        RFS("ПВС"),
        /**
         * ЛЕВАЯ ВИЛКА СВОБОДНА (Left Fork is Free)
         */
        LFS("ЛВС");
        companion object {
            fun conditionFromToken(token: String): Conditions? =
                Conditions.values().firstOrNull { it.key == token }
        }
    }

    enum class Action(val key: String) {
        STT("ШАГКС"),
        SFT("ШАГОС");

        companion object {
            fun actionFromToken(token: String): Action? =
                Action.values().firstOrNull { it.key == token }
        }
    }
}

class Executor internal constructor(
    private val philosopher: Philosopher,
    private val actions: List<Action>,
) {

    private sealed interface Item {
        data class Action(val action: () -> Unit) : Item
        object Empty : Item
    }

    private val channel: Channel<Item> = Channel()

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _isRunning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    init {
        scope.launch {
            channel.send(Item.Empty)
            startSuspend()
        }

        scope.launch {
            for (element in channel) {
                println(element)
                while (true) {
                    if (isRunning.value) {
                        if (element is Item.Action) {
                            delay(1000)
                            element.action.invoke()
                        } else {
                            delay(100)
                        }
                        break
                    }
                }
            }
        }
    }
    fun start() {
        _isRunning.update { true }
    }

    fun stop() {
        _isRunning.update { false }
    }

    suspend fun startSuspend() {
        actions.forEach { action ->
            startAction(action)
        }
        _isRunning.update { false }
    }

    suspend fun startAction(action: Action) {
        when (action) {
            is Action.Command -> {
                when (val com = action.command) {
                    is Command.Queue -> { }
                    is Command.SymbolCommand -> {
                        val action = Commands.Action.actionFromToken(com.symbol)
                            ?: error("incorrect SymbolCommand with ${com.symbol}")
                        when (action) {
                            Commands.Action.STT -> {
                                { philosopher.stepToTable() }.addOnQueue()
                            }
                            Commands.Action.SFT -> {
                                { philosopher.stepFromTable() }.addOnQueue()
                            }
                        }
                    }
                    is Command.Together -> { }
                    Command.DOWN -> { }
                    Command.LEFT -> { }
                    Command.PASS -> { }
                    Command.RIGHT -> { }
                    Command.START -> { }
                    Command.STEP -> { }
                    Command.STOP -> { }
                    Command.UP -> { }
                }
            }

            is Action.If -> {
                // TODO
            }

            is Action.Pause -> {
                // TODO
            }

            is Action.Repeat -> {
                // TODO
            }

            is Action.Wait -> {
                // TODO
            }

            is Action.While -> startWhile(action)
        }
    }

    suspend fun startWhile(node: Action.While) {
        while (true) {
            channel.send(Item.Empty)
            val isTrue = when (val cond = node.condition) {
                is Condition.NamedCondition -> {
                    val condition = Commands.Conditions.conditionFromToken(cond.name)
                        ?: error("Invalid condition with name = ${cond.name}")
                    println()
                    println("calculate condition $condition for state = ${philosopher.state.value.localizedString}")
                    when (condition) {
                        Commands.Conditions.CFS -> philosopher.CFS.value
                        Commands.Conditions.CBS -> philosopher.CBS.value
                        Commands.Conditions.RFS -> philosopher.RFS.value
                        Commands.Conditions.LFS -> philosopher.LFS.value
                    }
                }
            }
            println("condition result = $isTrue")
            if (isTrue) {
                println("startAction")
                startAction(node.action)
            } else {
                break
            }
        }
    }

    private suspend fun (() -> Unit).addOnQueue() {
        channel.send(Item.Action(this))
    }
}