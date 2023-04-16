package ru.nsu.synchro.app.machine.dsl

import ru.nsu.synchro.app.machine.ast.DelayNode
import ru.nsu.synchro.app.machine.ast.EnvNode
import ru.nsu.synchro.app.machine.ast.ExpressionNode
import ru.nsu.synchro.app.machine.ast.ExpressionType
import ru.nsu.synchro.app.machine.ast.ForeignFunctionNode
import ru.nsu.synchro.app.machine.ast.ParallelNode
import ru.nsu.synchro.app.machine.ast.RepeatNode
import ru.nsu.synchro.app.machine.ast.SynchronousNode
import ru.nsu.synchro.app.machine.ast.WhileNode
import kotlin.jvm.JvmName
import kotlin.time.Duration

typealias ParallelProgramBuilder = ProgramBuilder
typealias SynchronousProgramBuilder = ProgramBuilder

@ParallelDsl
open class ProgramBuilder(
    @PublishedApi
    internal val onAdd: (ExpressionNode) -> Unit
) {

    @ParallelDsl
    inline fun parallel(name: String? = null, block: ParallelProgramBuilder.() -> Unit) {
        val expressions = childParallelProgram(block)
        val node = ParallelNode(name, expressions)
        onAdd(node)
    }

    @JvmName("implicitParallel")
    @ParallelDsl
    inline fun String.parallel(block: ParallelProgramBuilder.() -> Unit) =
        parallel(name = this, block)

    @ParallelDsl
    inline fun synchronous(name: String?, block: SynchronousProgramBuilder.() -> Unit) {
        val expressions = childParallelProgram(block)
        val node = SynchronousNode(name, expressions)
        onAdd(node)
    }

    @JvmName("implicitSynchronous")
    @ParallelDsl
    inline fun String.synchronous(block: SynchronousProgramBuilder.() -> Unit) {
        synchronous(name = this, block)
    }

    @ParallelDsl
    inline fun whileLoop(condition: ExpressionNode, block: SynchronousProgramBuilder.() -> Unit) {
        val expressions = childParallelProgram(block)
        val node = WhileNode(condition, expressions)
        onAdd(node)
    }

    @ParallelDsl
    inline fun whileLoop(envName: String, block: SynchronousProgramBuilder.() -> Unit) {
        whileLoop(EnvNode(envName, ExpressionType.Boolean), block)
    }

    @ParallelDsl
    inline fun repeatLoop(amount: Int, block: SynchronousProgramBuilder.() -> Unit) {
        val expressions = childParallelProgram(block)
        val node = RepeatNode(amount, expressions)
        onAdd(node)
    }

    fun delay(duration: Duration) {
        val node = DelayNode(duration)
        onAdd(node)
    }

    @ParallelDsl
    operator fun String.invoke() {
        val node = ForeignFunctionNode(
            name = this,
            returnType = ExpressionType.NullableAny
        )
        onAdd(node)
    }
}

@PublishedApi
@ParallelDsl
internal inline fun childParallelProgram(block: ProgramBuilder.() -> Unit): List<ExpressionNode> {
    val nodes = buildList {
        ProgramBuilder { node ->
            add(node)
        }.apply(block)
    }

    return nodes
}

@ParallelDsl
inline fun program(
    name: String? = null,
    block: ParallelProgramBuilder.() -> Unit
): ParallelProgram {
    val nodes = buildList {
        ProgramBuilder { node ->
            add(node)
        }.apply(block)
    }

    return ParallelProgram(name, returns = null, nodes)
}
