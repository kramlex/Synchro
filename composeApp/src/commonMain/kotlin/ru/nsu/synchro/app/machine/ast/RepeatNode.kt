package ru.nsu.synchro.app.machine.ast

data class RepeatNode(
    val amount: Int,
    val expressions: List<ExpressionNode>
) : StatementNode
