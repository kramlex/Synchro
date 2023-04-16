package ru.nsu.synchro.app.machine.dsl

import ru.nsu.synchro.app.machine.ast.ExpressionNode
import ru.nsu.synchro.app.machine.ast.ParallelNode

data class ParallelProgram(
    val name: String?,
    val returns: String?,
    val expressions: List<ExpressionNode>
) {
    val rootNode: ParallelNode = ParallelNode(
        name = null,
        expressions = expressions
    )

    @ParallelDsl
    fun returns(string: String): ParallelProgram = copy(
        returns = string
    )
}
