package ru.nsu.synchro.app.machine.runtime

import ru.nsu.synchro.app.machine.dsl.ParallelProgram

suspend fun executeProgram(
    environment: ProgramEnvironment,
    program: ParallelProgram,
    debug: Boolean = false
) {
    val debugger = Debugger(enabled = debug)

    executeExpression(
        node = program.rootNode,
        runtime = Runtime(environment, debugger)
    )

    println(program.returns ?: return)
}
