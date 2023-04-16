package ru.nsu.synchro.app.machine.ast

data class ParallelNode(
    override val name: String? = null,
    override val expressions: List<ExpressionNode>
) : ControlFlowNode
