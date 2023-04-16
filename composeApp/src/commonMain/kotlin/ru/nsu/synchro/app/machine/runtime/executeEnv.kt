package ru.nsu.synchro.app.machine.runtime

import ru.nsu.synchro.app.machine.ast.EnvNode

suspend fun executeEnv(
    node: EnvNode,
    runtime: Runtime
): Any? {
    val result = runtime.environment.provideEnvVariable(node)
    runtime.debugger.println("Requesting ${node.name} variable of type ${node.type.name}. Got $result")
    return result
}
