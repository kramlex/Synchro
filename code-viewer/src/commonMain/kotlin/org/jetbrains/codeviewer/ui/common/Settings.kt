package org.jetbrains.codeviewer.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import ru.nsu.synchro.ast.Program

class Settings(val onOpenProgram: (Program) -> Unit) {
    var fontSize by mutableStateOf(13.sp)
    val maxLineSymbols = 120
}