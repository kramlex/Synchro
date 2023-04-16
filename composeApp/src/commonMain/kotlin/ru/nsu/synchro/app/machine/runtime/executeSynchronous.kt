package ru.nsu.synchro.app.machine.runtime

import ru.nsu.synchro.app.machine.ast.SynchronousNode

suspend fun executeSynchronous(
    node: SynchronousNode,
    runtime: Runtime
): Any? {
    runtime.debugger.println(node.name ?: "Anonymous synchronous")

    return node.expressions.fold<_, Any?>(Unit) { _, currentNode ->
        executeExpression(
            node = currentNode,
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
