package ru.nsu.synchro.app.machine.ast

sealed interface ControlFlowNode : StatementNode {
    val name: String?
    val expressions: List<ExpressionNode>
}
