package ru.nsu.synchro.app.machine.ast

data class SynchronousNode(
    override val name: String? = null,
    override val expressions: List<ExpressionNode>
) : ControlFlowNode {
    override val returnType = expressions.last().returnType
}
