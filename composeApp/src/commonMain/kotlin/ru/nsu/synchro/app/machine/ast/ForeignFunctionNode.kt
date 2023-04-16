package ru.nsu.synchro.app.machine.ast

data class ForeignFunctionNode(
    val name: String,
    override val returnType: ExpressionType
) : ExpressionNode
