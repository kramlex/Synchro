package ru.nsu.synchro.app.machine.runtime

import ru.nsu.synchro.app.machine.ast.RepeatNode

suspend fun executeRepeat(
    node: RepeatNode,
    runtime: Runtime
) {
    runtime.debugger.println("Repeat ${node.amount} times:")
    val debugger = runtime.debugger.nested()
    repeat(node.amount) {
        debugger.println("Number $it:")
        for (expression in node.expressions) {
            executeExpression(expression, runtime.copy(debugger = debugger.nested(prefix = "> ")))
        }
    }
}
