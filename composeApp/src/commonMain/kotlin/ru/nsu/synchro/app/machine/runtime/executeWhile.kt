package ru.nsu.synchro.app.machine.runtime

import ru.nsu.synchro.app.machine.ast.WhileNode

suspend fun executeWhile(
    node: WhileNode,
    runtime: Runtime
) {
    runtime.debugger.println("While:")
    val debugger = runtime.debugger.nested()
    while (true) {
        val condition = executeExpression(node.condition, runtime)
        if (condition !is Boolean) error("Expression result should be Boolean")
        runtime.debugger.println("${node.condition}: $condition")
        if (!condition) break
        for (expression in node.expressions) {
            executeExpression(expression, runtime.copy(debugger = debugger.nested(prefix = "> ")))
        }
    }
}
