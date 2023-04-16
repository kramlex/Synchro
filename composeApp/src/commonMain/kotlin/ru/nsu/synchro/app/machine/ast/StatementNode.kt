package ru.nsu.synchro.app.machine.ast

sealed interface StatementNode : ExpressionNode {
    override val returnType get() = ExpressionType.Unit
}
