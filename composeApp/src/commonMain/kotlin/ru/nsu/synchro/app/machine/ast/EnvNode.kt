package ru.nsu.synchro.app.machine.ast

data class EnvNode(val name: String, val type: ExpressionType) : ExpressionNode {
    override val returnType = type
}
