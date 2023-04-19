package ru.nsu.synchro.app.machine.runtime

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.nsu.synchro.app.machine.ast.ControlFlowNode
import ru.nsu.synchro.app.machine.ast.EnvNode
import ru.nsu.synchro.app.machine.ast.ExpressionType
import ru.nsu.synchro.app.machine.dsl.ParallelProgram

class ExecutableProgram(
    private val environment: ProgramEnvironment,
    private val program: ParallelProgram,
    private val debug: Boolean,
    runningThreads: MutableMap<String, MutableStateFlow<Boolean>>? = null,
    stackTrace: MutableStateFlow<List<String>>? = null
) {
    val debugger = Debugger(enabled = debug, stackTrace = stackTrace)

    val runningThreads: MutableMap<String, MutableStateFlow<Boolean>>

    init {
        if (runningThreads == null) {
            val newRunningThreads: MutableMap<String, MutableStateFlow<Boolean>> = mutableMapOf()
            for (expression in program.expressions) {
                val isRunning = MutableStateFlow(value = false)

                if (expression is ControlFlowNode) {
                    newRunningThreads[expression.name.orEmpty()] = isRunning
                }
            }
            this.runningThreads = newRunningThreads
        } else {
            this.runningThreads = runningThreads
        }
    }

    fun startThreads() {
        runningThreads.entries.forEach {
            it.value.update { true }
        }
    }

    fun startThread(threads: List<String>) {
        runningThreads.entries.mapNotNull {
            if (threads.contains(it.key)) {
                it
            } else {
                null
            }
        }.forEach { it.value.update { true } }
    }

    suspend fun startExecution() {
        coroutineScope {
            for (expression in program.expressions) launch {
                val name = (expression as? ControlFlowNode)?.name.orEmpty()
                val isRunning = runningThreads.getOrPut(name) {
                    MutableStateFlow(value = false)
                }

                executeExpression(
                    node = expression,
                    runtime = Runtime(name, environment, debugger, isRunning)
                )
            }
        }

        val returnedValue = program.returnedValue ?: return

        val conditionNode = EnvNode(returnedValue.conditionName, ExpressionType.Boolean)
        val returnedConditionValue = environment.provideEnvVariable(conditionNode) as? Boolean ?: return
        if (returnedConditionValue) {
            println(returnedValue.then)
        } else {
            println(returnedValue.otherwise)
        }
    }
}
