package ru.nsu.synchro.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.codeviewer.platform.HomeFolder
import org.jetbrains.codeviewer.ui.CodeViewer
import org.jetbrains.codeviewer.ui.CodeViewerMainView
import org.jetbrains.codeviewer.ui.common.Settings
import org.jetbrains.codeviewer.ui.editor.Editors
import org.jetbrains.codeviewer.ui.filetree.FileTree
import ru.nsu.synchro.app.gui.gameV2.PhilosophersParallelGame
import ru.nsu.synchro.app.gui.gameV2.ui.PhilosophersParallelGameBoard
import ru.nsu.synchro.app.parser.Parser
import ru.nsu.synchro.app.ui.theme.AppTheme
import ru.nsu.synchro.ast.Program

@Composable
internal fun App() = AppTheme {
    val parser: Parser by remember { mutableStateOf(Parser()) }
    var openedProgram: Program? by remember { mutableStateOf(null) }

    val codeViewer = remember {
        val editors = Editors(object : ru.nsu.synchro.code.parser.Parser {
            override fun parse(string: String): Program {
                return parser.parse(string)
            }

            override fun parseFromFile(filePath: String): Program {
                return parser.parseFromFile(filePath)
            }
        })
        CodeViewer(
            editors = editors,
            fileTree = FileTree(HomeFolder, editors),
            settings = Settings(onOpenProgram = { openedProgram = it })
        )
    }

    if (openedProgram != null) {
        PhilosophersParallelGameBoard(
            game = PhilosophersParallelGame(openedProgram!!, true),
            onBack = {
                openedProgram = null
            }
        )
    } else {
        CodeViewerMainView(codeViewer)
    }
}
