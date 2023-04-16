package ru.nsu.synchro.app.machine.runtime

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import ru.nsu.synchro.app.machine.ast.ParallelNode

suspend fun executeParallel(
    node: ParallelNode,
    runtime: Runtime,
) {
    runtime.debugger.println(node.name ?: "Anonymous parallel")

    node.expressions.map { expression ->
        runtime.scope.async {
            executeExpression(
                node = expression,
                runtime = runtime.copy(
                    debugger = runtime.debugger.nested(
                        prefix = buildString {
                            append("> ")
                            if (node.name != null) {
                                append("${node.name}: ")
                            }
                        }
                    )
                )
            )
        }
    }.awaitAll()
}
