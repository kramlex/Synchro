package ru.nsu.synchro.app.machine.runtime

import kotlinx.coroutines.delay
import ru.nsu.synchro.app.machine.ast.EnvNode
import ru.nsu.synchro.app.machine.ast.ExpressionType
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

    delay(200)
    val returnedValue = program.returnedValue ?: return

    val conditionNode = EnvNode(returnedValue.conditionName, ExpressionType.Boolean)
    val returnedConditionValue = environment.provideEnvVariable(conditionNode)
    println(conditionNode)
    println(returnedConditionValue)
    if (returnedConditionValue == true) {
        println(returnedValue.then)
    } else {
        println(returnedValue.otherwise)
    }
    println(program.returnedValue ?: return)
//    println(program.returns ?: return)
}
