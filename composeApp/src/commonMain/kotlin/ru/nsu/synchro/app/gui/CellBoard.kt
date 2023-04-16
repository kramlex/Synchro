package ru.nsu.synchro.app.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow

data class CellBoardSize(
    val width: Int,
    val height: Int
) {
    companion object {
        val empty: CellBoardSize = CellBoardSize(0, 0)
    }
}

@Composable
fun CellBoard(
    boardFlow: MutableStateFlow<CellBoardSize>,
) {

    val scope = rememberCoroutineScope()
    val size: State<CellBoardSize> = boardFlow.collectAsState(
        initial = CellBoardSize.empty,
        context = scope.coroutineContext
    )

    Column {
        (0..size.value.height).forEach {
            Row {
                (0..size.value.width).forEach {
                    Square()
                }
            }
        }
    }
}

@Composable
fun Square() {
    Box(
        modifier = Modifier
            .size(60.dp)
            .padding(4.dp)
            .background(Color.White)
            .border(width = 2.dp, color = Color.Black)
    ) {
        // You can put any content inside the square here
    }
}