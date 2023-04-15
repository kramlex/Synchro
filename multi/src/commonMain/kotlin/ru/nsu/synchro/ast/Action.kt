package ru.nsu.synchro.ast

sealed interface Action : AstNode {

    val name: Name?

    data class Command(
        override val name: Name? = null,
        val command: ru.nsu.synchro.ast.Command
    ) : Action

    data class If(
        override val name: Name? = null,
        val condition: Condition,
        val action: Action,
    ) : Action

    data class Wait(
        override val name: Name? = null,
        val waitActionName: Name,
    ) : Action

    data class Pause(
        override val name: Name? = null,
        val durationSec: Int,
    ) : Action

    data class Repeat(
        override val name: Name? = null,
        val action: Action,
        val repeatCount: Int,
    ) : Action

    data class While(
        override val name: Name? = null,
        val condition: Condition,
        val action: Action,
    ) : Action

    fun changeName(name: Name?) = when (this) {
        is If -> this.copy(name = name)
        is Pause -> this.copy(name = name)
        is Wait -> this.copy(name = name)
        is Command -> this.copy(name = name)
        is While -> this.copy(name = name)
        is Repeat -> this.copy(name = name)
    }
}
