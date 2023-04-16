package ru.nsu.synchro.app.machine.runtime

import ru.nsu.synchro.app.machine.ast.ForeignFunctionNode

suspend fun executeForeignFunction(
    node: ForeignFunctionNode,
    runtime: Runtime
): Any? {
    val result = runtime.environment.callForeignFunction(node)
    runtime.debugger.println("Calling ${node.name} function with return type ${node.returnType.name}. Got $result")
    return result
}
