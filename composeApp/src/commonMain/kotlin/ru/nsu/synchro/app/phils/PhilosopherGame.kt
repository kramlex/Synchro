package ru.nsu.synchro.app.phils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import ru.nsu.synchro.ast.Action
import ru.nsu.synchro.ast.Command
import ru.nsu.synchro.ast.Condition
import ru.nsu.synchro.app.machine.Executor

class PhilosopherGame(
    private val stepsCounts: List<Int>,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val philosophersCount = stepsCounts.size

    // fork index - phil number
    private val forksMap: MutableStateFlow<Map<Int, Int>> =
        MutableStateFlow(mapOf())
    val forks: StateFlow<Map<Int, Int>> =
        forksMap.asStateFlow()

    val philosopherAndExecutor: MutableStateFlow<List<Pair<Executor, Philosopher>>> =
        MutableStateFlow(emptyList())

    val states: StateFlow<Map<Int, Philosopher.State>> = philosopherAndExecutor.flatMapLatest { philosopherAndExecutor ->
        combine(philosopherAndExecutor.map { it.second.state }) { list ->
            list.mapIndexed { index, element ->
                Pair(index + 1, element)
            }.toMap()
        }
    }.stateIn(coroutineScope, SharingStarted.Eagerly, emptyMap())

    init {
        setup()
    }

    fun reset() {
        setup()
    }

    private fun setup() {
        if (philosophersCount in (1..5)) {
            philosopherAndExecutor.update {
                stepsCounts.mapIndexed { index, count ->
                    val philosopher = Philosopher(
                        number = index + 1,
                        stepCount = count,
                        forksMap = forksMap,
                        allCount = philosophersCount
                    )
                    val executor = Executor(
                        philosopher = philosopher,
                        actions = listOf(
                            Action.While(
                                condition = Condition.NamedCondition("КВС"),
                                action = Action.Command(command = Command.SymbolCommand("ШАГКС"))
                            )
                        )
                    )
                    Pair(executor, philosopher)
                }
            }

        } else error("Illegal philosophers count")
    }
}
