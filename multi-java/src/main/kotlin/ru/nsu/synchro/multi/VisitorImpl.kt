@file:Suppress("NAME_SHADOWING")

package ru.nsu.synchro.multi

import MultiBaseVisitor
import MultiParser
import ru.nsu.synchro.ast.Action
import ru.nsu.synchro.ast.AstNode
import ru.nsu.synchro.ast.Command
import ru.nsu.synchro.ast.Condition
import ru.nsu.synchro.ast.Context
import ru.nsu.synchro.ast.Data
import ru.nsu.synchro.ast.Flow
import ru.nsu.synchro.ast.Name
import ru.nsu.synchro.ast.Number
import ru.nsu.synchro.ast.Program
import ru.nsu.synchro.ast.QuotedString
import ru.nsu.synchro.ast.Set
import ru.nsu.synchro.ast.Size
import ru.nsu.synchro.ast.Text

class VisitorImpl: MultiBaseVisitor<AstNode>() {
    override fun visitProg(ctx: MultiParser.ProgContext?): AstNode {
        return Program(
            context = ctx?.context()?.let { visitContext(it) as Context } ,
            set = ctx?.set().let { visitSet(it) as Set },
            data = Number(5.5)
        )
    }

    override fun visitContext(ctx: MultiParser.ContextContext?): AstNode {
        val mappedElements: List<Pair<Name, Data>> = ctx?.contextElement()?.map { element ->
            when (element) {
                is MultiParser.BoardExprContext -> {
                    val name: Name = element.BOARD().text
                    val ints: List<Int> = element.boardSize().INT().mapNotNull { it.text.toIntOrNull() }
                    if (ints.size != 2) error("board size not contains two elements")
                    val data: Data = Size(ints.first(), ints.last())
                    Pair(name, data)
                }
                is MultiParser.AsignExprContext -> {
                    val name: Name = element.SYMBOL().text
                    val data: Data = visitTypedValue(element.typedValue())
                    Pair(name, data)
                }
                else -> error("invalid context element")
            }
        } ?: emptyList()
        return Context(
            map = mappedElements.associate { it.first to it.second }
        )
    }

    private fun visitTypedValue(ctx: MultiParser.TypedValueContext): Data {
        return when (ctx) {
            is MultiParser.IntTypedValueContext -> visitIntTypedValue(ctx)
            is MultiParser.DoubleTypedValueContext -> visitDoubleTypedValue(ctx)
            is MultiParser.SymbolTypedValueContext -> visitSymbolTypedValue(ctx)
            is MultiParser.StringTypedValueContext -> visitStringTypedValue(ctx)
            else -> error("incorrect TypedValueContext")
        } as? Data ?: error("visitTypedValue failure convert to data")
    }

    override fun visitIntTypedValue(ctx: MultiParser.IntTypedValueContext?): AstNode {
        val ctx = ctx ?: error("IntTypedValueContext is null")
        return Number(ctx.INT().text.toInt())
    }

    override fun visitDoubleTypedValue(ctx: MultiParser.DoubleTypedValueContext?): AstNode {
        val ctx = ctx ?: error("DoubleTypedValueContext is null")
        return Number(ctx.DOUBLE().text.toDouble())
    }

    override fun visitSymbolTypedValue(ctx: MultiParser.SymbolTypedValueContext?): AstNode {
        val ctx = ctx ?: error("SymbolTypedValueContext is null")
        return Text(ctx.text)
    }

    override fun visitStringTypedValue(ctx: MultiParser.StringTypedValueContext?): AstNode {
        val ctx = ctx ?: error("StringTypedValueContext is null")
        return QuotedString(ctx.text.removeSurrounding("\"", "\""))
    }

    override fun visitSet(ctx: MultiParser.SetContext?): AstNode? {
        val context = ctx ?: return null
        val name: String? = context.name?.text

        val flows = context.thread().mapNotNull { threadContext ->
            visitThread(threadContext) as? Flow
        }
        return Set(name = name, flows = flows)
    }

    override fun visitThread(ctx: MultiParser.ThreadContext?): AstNode? {
        val context = ctx ?: return null
        val threadName: String? = context.name?.text
        val actions: List<Action> = context.action().mapNotNull { actionContext ->
            visitAction(actionContext) as? Action
        }
        return Flow(name = threadName, actions = actions)
    }

    override fun visitAction(ctx: MultiParser.ActionContext?): AstNode? {
        val context = ctx ?: return null
        val actionName = context.name?.text
        val body: Action? = when (val body = context.body) {
            is MultiParser.CommandActionBodyContext -> visitCommandActionBody(body)
            is MultiParser.IfThenActionBodyContext -> visitIfThenActionBody(body)
            is MultiParser.WaitActionBodyContext -> visitWaitActionBody(body)
            is MultiParser.PauseActionBodyContext -> visitPauseActionBody(body)
            is MultiParser.RepeatBodyContext -> visitRepeatBody(body)
            is MultiParser.ForWhileBodyContext -> visitForWhileBody(body)
            else -> null
        } as? Action
        return body?.changeName(name = actionName)
    }

    // action bodies
    override fun visitCommandActionBody(ctx: MultiParser.CommandActionBodyContext?): AstNode? {
        val context = ctx ?: return null
        val command = when (val command = context.command()) {
            is MultiParser.BaseCommandContext -> visitBaseCommand(command)
            is MultiParser.TogetherComandContext -> visitTogetherComand(command)
            is MultiParser.QueueCommandContext -> visitQueueCommand(command)
            is MultiParser.SymbolCommandContext -> visitSymbolCommand(command)
            else -> null
        } as? Command ?: error("incorrect Command")
        return Action.Command(name = null, command = command)
    }

    // Commands
    override fun visitBaseCommand(ctx: MultiParser.BaseCommandContext?): AstNode? {
        val context = ctx ?: return null
        return if (context.base_command()?.START() != null) {
            Command.START
        } else if (context.base_command()?.STOP() != null) {
            Command.STOP
        } else if (context.base_command()?.LEFT() != null) {
            Command.LEFT
        } else if (context.base_command()?.RIGHT() != null) {
            Command.RIGHT
        } else if (context.base_command()?.UP() != null) {
            Command.UP
        } else if (context.base_command()?.DOWN() != null) {
            Command.DOWN
        } else {
            null
        }
    }

    override fun visitTogetherComand(ctx: MultiParser.TogetherComandContext?): AstNode? {
        val togetherContext = ctx?.together_command() ?: return null
        val actions: List<Action> = togetherContext.action().mapNotNull { actionContext ->
            visitAction(actionContext) as? Action
        }
        return Command.Together(actions)
    }

    override fun visitQueueCommand(ctx: MultiParser.QueueCommandContext?): AstNode? {
        val queueContext = ctx?.queue_command() ?: return null
        val action: List<Action> = queueContext.action().mapNotNull { actionContext ->
            visitAction(actionContext) as? Action
        }
        return Command.Queue(action)
    }

    override fun visitSymbolCommand(ctx: MultiParser.SymbolCommandContext?): AstNode? {
        val symbolContext = ctx ?: return null
        return Command.SymbolCommand(symbolContext.SYMBOL().text)
    }

    // End Commands

    override fun visitIfThenActionBody(ctx: MultiParser.IfThenActionBodyContext?): AstNode? {
        val context = ctx ?: return null
        return Action.If(
            name = null,
            condition = (visitCondition(context.condition()) as? Condition) ?: error("incorrect condition"),
            action = (visitAction(context.action()) as? Action) ?: error("incorrect action")
        )
    }

    override fun visitCondition(ctx: MultiParser.ConditionContext?): AstNode? {
        val context = ctx ?: return null
        val namedConditionValue = context.namedCondition.text
        return Condition.NamedCondition(namedConditionValue)
    }

    override fun visitWaitActionBody(ctx: MultiParser.WaitActionBodyContext?): AstNode? {
        val context = ctx ?: return null
        val waitActionName: Name = context.waitName.text
        return Action.Wait(name = null, waitActionName = waitActionName)
    }

    override fun visitPauseActionBody(ctx: MultiParser.PauseActionBodyContext?): AstNode? {
        val context = ctx ?: return null
        return Action.Pause(name = null, durationSec = context.durationSec.text.toInt())
    }

    override fun visitForWhileBody(ctx: MultiParser.ForWhileBodyContext?): AstNode? {
        val context = ctx ?: return null
        return Action.While(
            name = null,
            action = (visitAction(context.repeatAction) as? Action) ?: error("incorrect action"),
            condition = (visitCondition(context.whileCondition) as? Condition) ?: error("incorrect condition"),
        )
    }

    override fun visitRepeatBody(ctx: MultiParser.RepeatBodyContext?): AstNode? {
        val context = ctx ?: return null
        return Action.Repeat(
            name = null,
            action = (visitAction(context.repeatAction) as? Action) ?: error("incorrect action"),
            repeatCount = context.repeatCount?.text?.toInt() ?: error("invalid repeat count")
        )
    }

    // end action bodies

}