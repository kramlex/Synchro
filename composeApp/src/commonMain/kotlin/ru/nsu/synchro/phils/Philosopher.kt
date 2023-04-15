@file:Suppress("MemberVisibilityCanBePrivate")

package ru.nsu.synchro.phils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class Philosopher(
    val number: Int,
    val stepCount: Int,
    val forksMap: MutableStateFlow<Map<Int, Int>>,
    val allCount: Int,
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _state: MutableStateFlow<InternalState> =
        MutableStateFlow(InternalState.Step(0))

    private val _CFS: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val CFS: StateFlow<Boolean> = _CFS.asStateFlow()
    private val _CBS: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val CBS: StateFlow<Boolean> = _CBS.asStateFlow()
    private val _RFS: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val RFS: StateFlow<Boolean> = _RFS.asStateFlow()
    private val _LFS: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val LFS: StateFlow<Boolean> = _LFS.asStateFlow()

    val state: StateFlow<State> = combine(
        _state, forksMap
    ) { state, forks ->
        if (state is InternalState.Step) {
            if (state.stepNumber == stepCount - 1) {
                val rightForkNumber = (number - 1).takeIf { it != 0 } ?: allCount
                val leftForkNumber = number

                val haveRightFork = forks[rightForkNumber] == number
                val haveLeftFork = forks[leftForkNumber] == number

                if (haveRightFork && haveLeftFork) {
                    State.HaveAllForks
                } else if (haveRightFork) {
                    State.HaveRightFork
                } else if (haveLeftFork) {
                    State.HaveLeftFork
                } else {
                    State.Step(state.stepNumber)
                }
            } else {
                State.Step(state.stepNumber)
            }
        } else {
            State.Eating
        }
    }.stateIn(scope, SharingStarted.Eagerly, State.Step(0))

    val validState: StateFlow<List<State>> = combine(state, forksMap) { state, map ->
        validNextState(state, map)
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    init {
        state.onEach {
            println("update conditions = ${stepToTableIsValid()} for state = ${it.localizedString}")
            println()
            _CFS.update { stepToTableIsValid() }
            _CBS.update { stepFromTableIsValid() }
            _RFS.update { canTakeRightFork() }
            _LFS.update { canTakeLeftFork() }
        }.launchIn(scope)
    }

    fun validNextState(state: State, forks: Map<Int, Int>): List<State> {
        val rightForkNumber = (number - 1).takeIf { it != 0 } ?: allCount
        val leftForkNumber = number

        return when (state) {
            State.Eating -> listOf(State.HaveAllForks)
            State.HaveAllForks -> listOf(State.Eating)
            State.HaveLeftFork -> listOf(State.HaveLeftFork)
            State.HaveRightFork -> listOf(State.HaveRightFork)
            is State.Step -> {
                if (state.stepNumber == stepCount - 1) {
                    listOf(
                        if (forks[rightForkNumber] == null) State.HaveRightFork else null,
                        if (forks[leftForkNumber] == null) State.HaveLeftFork else null
                    ).mapNotNull { it }
                } else {
                    listOf(State.Step(stepNumber = state.stepNumber + 1))
                }
            }
        }
    }

    // Шаг к столу возможен
    fun stepToTableIsValid(): Boolean =
        (state.value as? State.Step)?.let {
            it.stepNumber < stepCount - 1
        } ?: false

    // Шаг от стола возможен
    fun stepFromTableIsValid(): Boolean =
        (state.value as? State.Step)?.let {
            it.stepNumber - 1 >= 0
        } ?: false

    // Шаг к столу
    fun stepToTable() {
        val currentState = _state.value
        if (currentState is InternalState.Step && stepToTableIsValid()) {
            _state.update { InternalState.Step(currentState.stepNumber + 1) }
        } else {
            error("Illegal philosopher action")
        }
    }

    // Шаг от стола
    fun stepFromTable() {
        val currentState = _state.value
        if (currentState is InternalState.Step && stepFromTableIsValid()) {
            _state.update { InternalState.Step(currentState.stepNumber - 1) }
        } else {
            error("Illegal philosopher action")
        }
    }

    // Начать есть
    fun startEating() {
        val currentState = state.value
        if (currentState is State.HaveAllForks) {
            _state.update { InternalState.Eating }
        } else {
            error("Illegal philosopher action")
        }
    }

    fun startEatingIsValid(): Boolean {
        return state.value is State.HaveAllForks
    }

    fun endEating() {
        val currentState = state.value
        if (currentState is State.Eating) {
            _state.update { InternalState.Step(stepCount - 1) }
        } else {
            error("Illegal philosopher action")
        }
    }

    fun endEatingIsValid(): Boolean {
        return state.value is State.Eating
    }

    fun canTakeRightFork(): Boolean {
        val currentState = _state.value
        return if (currentState is InternalState.Step && currentState.stepNumber == stepCount - 1) {
            val rightForkNumber = (number - 1).takeIf { it != 0 } ?: allCount
            forksMap.value[rightForkNumber] == null
        } else {
            false
        }
    }

    fun canTakeLeftFork(): Boolean {
        val currentState = _state.value
        return if (currentState is InternalState.Step && currentState.stepNumber == stepCount - 1) {
            val leftForkNumber = number
            forksMap.value[leftForkNumber] == null
        } else {
            false
        }

    }

    fun takeLeftFork() {
        if (!canTakeLeftFork()) error("Illegal philosopher action")
        val leftForkNumber = number
        forksMap.update { it + Pair(leftForkNumber, number) }
    }

    fun takeRightFork() {
        if (!canTakeRightFork()) error("Illegal philosopher action")
        val rightForkNumber = (number - 1).takeIf { it != 0 } ?: allCount
        forksMap.update { it + Pair(rightForkNumber, number) }
    }

    fun putBackLeftFork() {
        val currentState = state.value
        val leftForkNumber = number
        if (currentState is State.HaveLeftFork || currentState is State.HaveAllForks) {
            forksMap.update { it - leftForkNumber }
        } else error("Illegal philosopher action")
    }

    fun putBackRightFork() {
        val currentState = state.value
        val rightForkNumber = (number - 1).takeIf { it != 0 } ?: allCount
        if (currentState is State.HaveRightFork || currentState is State.HaveAllForks) {
            forksMap.update { it - rightForkNumber }
        } else error("Illegal philosopher action")
    }

    fun putBackLeftForkIsValid(): Boolean {
        val currentState = _state.value
        return if (currentState is InternalState.Eating) {
            false
        } else {
            val leftForkNumber = number
            forksMap.value[leftForkNumber] == number
        }
    }

    fun putBackRightForkIsValid(): Boolean {
        val currentState = _state.value
        return if (currentState is InternalState.Eating) {
            false
        } else {
            val rightForkNumber = (number - 1).takeIf { it != 0 } ?: allCount
            forksMap.value[rightForkNumber] == number
        }
    }

    internal sealed interface InternalState {
        object Eating : InternalState
        data class Step(val stepNumber: Int) : InternalState
    }

    sealed interface State {
        object Eating : State
        data class Step(val stepNumber: Int) : State
        object HaveLeftFork : State
        object HaveRightFork : State
        object HaveAllForks : State

        val localizedString: String
            get() = when (this) {
                Eating -> "Ест"
                HaveAllForks -> "Есть две вилки"
                HaveLeftFork -> "Есть левая вилка"
                HaveRightFork -> "Есть правая вилка"
                is Step -> "Шаг #${stepNumber}"
            }
    }
}
