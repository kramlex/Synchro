package ru.nsu.synchro.app.machine.ast

sealed interface ExpressionNode {
    val returnType: ExpressionType
}
