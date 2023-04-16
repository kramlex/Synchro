package ru.nsu.synchro.app.machine.runtime

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.nsu.synchro.app.machine.ast.ParallelNode

suspend fun executeParallel(
    node: ParallelNode,
    runtime: Runtime,
) {
    runtime.debugger.println(node.name ?: "Anonymous parallel")

    coroutineScope {
        for (expression in node.expressions) launch {
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
    }
}
