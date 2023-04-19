package ru.nsu.synchro.app.machine.runtime

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import ru.nsu.synchro.app.machine.ast.DelayNode
import ru.nsu.synchro.app.machine.ast.EnvNode
import ru.nsu.synchro.app.machine.ast.ExpressionNode
import ru.nsu.synchro.app.machine.ast.ExpressionType
import ru.nsu.synchro.app.machine.ast.ForeignFunctionNode
import ru.nsu.synchro.app.machine.ast.ParallelNode
import ru.nsu.synchro.app.machine.ast.RepeatNode
import ru.nsu.synchro.app.machine.ast.SynchronousNode
import ru.nsu.synchro.app.machine.ast.WhileNode

suspend fun executeExpression(
    node: ExpressionNode,
    runtime: Runtime
): Any? {
    runtime.isRunning.first { isRunning -> isRunning }
    return when (node) {
        is EnvNode -> executeEnv(node, runtime)
        is ForeignFunctionNode -> {
            executeForeignFunction(node, runtime)
            delay(1000L)
        }
        is ParallelNode -> executeParallel(node, runtime)
        is SynchronousNode -> executeSynchronous(node, runtime)
        is DelayNode -> {
            delay(1000L)
            executeDelay(node, runtime)
        }
        is RepeatNode -> executeRepeat(node, runtime)
        is WhileNode -> executeWhile(node, runtime)
    }
}

fun checkExpressionResult(result: Any?, type: ExpressionType) =
    require(
        when (type) {
            ExpressionType.Boolean -> result is Boolean
            ExpressionType.Unit -> result is Unit
            else -> true
        }
    )
