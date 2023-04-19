package org.jetbrains.codeviewer.ui.editor

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.codeviewer.platform.File
import org.jetbrains.codeviewer.util.EmptyTextLines
import org.jetbrains.codeviewer.util.SingleSelection
import ru.nsu.synchro.code.parser.Parser
import ru.nsu.synchro.code.program.Program

class Editor internal constructor(
    internal val program: Program,
    val fileName: String,
    val lines: (backgroundScope: CoroutineScope) -> Lines,
) {

    var close: (() -> Unit)? = null
    lateinit var selection: SingleSelection

    val isActive: Boolean
        get() = selection.selected === this

    fun activate() {
        selection.selected = this
    }

    class Line(val number: Int, val content: Content)

    interface Lines {
        val lineNumberDigitCount: Int get() = size.toString().length
        val size: Int
        operator fun get(index: Int): Line
    }

    class Content(val value: State<String>, val isCode: Boolean, val isSynchroProgram: Boolean)
}

fun Editor(file: File, parser: Parser) = Editor(
    fileName = file.name,
    program = try {
        val program = parser.parseFromFile(file.fullPath)
        Program.Synchro(program)
    } catch (error: Throwable) {
        Program.None
    }
) { backgroundScope ->
    val textLines = try {
        file.readLines(backgroundScope)
    } catch (e: Throwable) {
        e.printStackTrace()
        EmptyTextLines
    }
    val isCode = file.name.endsWith(".kt", ignoreCase = true)
    val isSynchroProgramByExt = file.name.endsWith(".synchro", ignoreCase = true)

    fun content(index: Int): Editor.Content {
        val text = textLines.get(index)
        val state = mutableStateOf(text)
        return Editor.Content(state, isCode, isSynchroProgramByExt)
    }

    object : Editor.Lines {
        override val size get() = textLines.size

        override fun get(index: Int) = Editor.Line(
            number = index + 1,
            content = content(index)
        )
    }
}
