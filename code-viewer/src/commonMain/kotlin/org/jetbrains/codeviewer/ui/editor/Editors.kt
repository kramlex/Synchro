package org.jetbrains.codeviewer.ui.editor

import androidx.compose.runtime.mutableStateListOf
import org.jetbrains.codeviewer.platform.File
import org.jetbrains.codeviewer.util.SingleSelection
import ru.nsu.synchro.code.parser.Parser

class Editors(private val parser: Parser) {
    private val selection = SingleSelection()

    var editors = mutableStateListOf<Editor>()
        private set

    val active: Editor? get() = selection.selected as Editor?

    fun open(file: File) {
        val editor = Editor(file, parser)
        editor.selection = selection
        editor.close = {
            close(editor)
        }
        editors.add(editor)
        editor.activate()
    }

    private fun close(editor: Editor) {
        val index = editors.indexOf(editor)
        editors.remove(editor)
        if (editor.isActive) {
            selection.selected = editors.getOrNull(index.coerceAtMost(editors.lastIndex))
        }
    }
}