package ru.nsu.synchro.ast

sealed interface Condition: AstNode {

    data class NamedCondition(
        val name: Name
    ): Condition
}
