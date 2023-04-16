package ru.nsu.synchro.app.machine.runtime

import kotlinx.coroutines.delay
import ru.nsu.synchro.app.machine.ast.DelayNode

suspend fun executeDelay(
    node: DelayNode,
    runtime: Runtime
) {
    runtime.debugger.println("Sleeping for ${node.delayDuration}...")
    delay(node.delayDuration)
    runtime.debugger.println("Woke up!")
}
