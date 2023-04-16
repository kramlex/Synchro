package ru.nsu.synchro.app.machine.ast

data class WhileNode(
    val condition: ExpressionNode,
    val expressions: List<ExpressionNode>
) : StatementNode {
    init {
        require(condition.returnType == ExpressionType.Boolean)
    }
}
