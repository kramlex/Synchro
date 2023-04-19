@file:Suppress("FunctionName")

package ru.nsu.synchro.app.gui.gameV2.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.codeviewer.platform.VerticalScrollbar
import ru.nsu.synchro.app.gui.gameV2.PhilosophersParallelGame
import ru.nsu.synchro.app.utils.collectAsMutableState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private enum class Position {
    LEFT, TOP, RIGHT, BOTTOM
}

private data class Cell(
    val index: Int,
    val position: Position,
    val center: Offset,
    val size: Float,
) {
    val radius: Float get() = size / 2
}

@Composable
fun PhilosophersParallelGameBoard(
    game: PhilosophersParallelGame,
    onBack: () -> Unit
) {

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(null) {
        scope.launch {
            game.startExecution()
        }
    }

    Row {
        Column(modifier = Modifier.padding(4.dp)) {
            PhilosophersBoard(
                game = game
            )
        }
        Column {
            Column {
                RunInfo(
                    game = game,
                    onBack = onBack
                )
                Divider()
                PhilosophersInfo(game)
                Divider()
                DebugPanel(game)
            }
        }
    }
}

@Composable
fun DebugPanel(game: PhilosophersParallelGame) {
    val coroutineScope = rememberCoroutineScope()
    val stackTrace: List<String> by game.stackTrace.collectAsState()
    println(stackTrace)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(8.dp)
    ) {
        val scrollState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState
        ) {
            items(stackTrace) {
                Text(it)
            }
            if (stackTrace.isNotEmpty()) {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(stackTrace.lastIndex)
                }
            }
        }



        VerticalScrollbar(
            Modifier.align(Alignment.CenterEnd),
            scrollState
        )
    }
}

@Composable
fun RunInfo(
    game: PhilosophersParallelGame,
    onBack: () -> Unit,
) {
    val allThreadIsRunning: Boolean by game.allThreadIsRunning.collectAsState()

    Row {
        TextButton(
            onClick = onBack,
        ) {
            Text("Назад")
        }
        TextButton(
            onClick = { game.runAllThreads() },
            enabled = !allThreadIsRunning
        ) {
            Text("Запустить все потоки")
        }
        TextButton(
            onClick = { game.stopAllThreads() },
            enabled = allThreadIsRunning
        ) {
            Text("Остановить все потоки")
        }
    }
}

@Composable
fun PhilosophersInfo(game: PhilosophersParallelGame) {
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        game.philosophers.forEachIndexed { index, philosopher ->
            PhilosopherInfo(philosopher)
            if (index != game.philosophers.lastIndex) {
                Divider()
            }
        }
    }

}

@Composable
fun PhilosopherInfo(philosopher: PhilosophersParallelGame.Philosopher) {
    val isRunning: MutableState<Boolean> = philosopher.isRunning.collectAsMutableState()
    Row {
        TextButton(
            onClick = { isRunning.value = true },
            enabled = !isRunning.value
        ) {
            Text("Запустить")
        }
        TextButton(
            onClick = { isRunning.value = false },
            enabled = isRunning.value
        ) {
            Text("Остановить")
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun PhilosophersBoard(
    game: PhilosophersParallelGame
) {
    val philosophers by remember { mutableStateOf(game.philosophers)  }

    val philosophersPositions by game.philosopherPositions.collectAsState()
    val busyForks by game.busyForks.collectAsState()

    var cellsMap: Map<Int, List<Cell>> by remember {
        mutableStateOf(mapOf())
    }

    val textMeasure = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1.0f)
            .background(Color.Blue)
    ) {

        val tableRadiusPercent = 0.14f

//        println("[DRAW TABLE]")
        val tableRadius = size.width * tableRadiusPercent
        drawCircle(
            color = Color.Black,
            radius = tableRadius,
            center = center,
            style = Fill
        )
        drawCircle(
            color = Color.White,
            radius = tableRadius * 0.25f,
            center = center.plus(Offset(x = -tableRadius * 0.6f, y = 0f)),
            style = Fill
        )
        drawCircle(
            color = Color.White,
            radius = tableRadius * 0.25f,
            center = center.plus(Offset(x = tableRadius * 0.6f, y = 0f)),
            style = Fill
        )
        if (philosophers.size >= 3) {
            drawCircle(
                color = Color.White,
                radius = tableRadius * 0.25f,
                center = center.plus(Offset(x = 0f, y = -tableRadius * 0.6f)),
                style = Fill
            )
        }
        if (philosophers.size == 4) {
            drawCircle(
                color = Color.White,
                radius = tableRadius * 0.25f,
                center = center.plus(Offset(x = 0f, y = tableRadius * 0.6f)),
                style = Fill
            )
        }

//        println("[DRAW FORKS]")

        val startOffset = tableRadius * 0.2f
        val endOffset = tableRadius * 0.8f

        if (!busyForks.contains(0)) {
            if (philosophers.size == 2) {
                drawLine(
                    color = Color.LightGray,
                    start = center.minus(Offset(x = .0f, y = startOffset)),
                    end =  center.minus(Offset(x = .0f, y = endOffset))
                )
            } else {
                val angle = 3f * PI.toFloat() / 4f
                drawLine(
                    color = Color.LightGray,
                    start = center.plus(Offset(x = cos(angle) * startOffset, y = -sin(angle) * startOffset)),
                    end =  center.plus(Offset(x = cos(angle) * endOffset, y = -sin(angle) * endOffset))
                )
            }
        }
        if (!busyForks.contains(1)) {
            if (philosophers.size == 2) {
                drawLine(
                    color = Color.LightGray,
                    start = center.plus(Offset(x = .0f, y = startOffset)),
                    end =  center.plus(Offset(x = .0f, y = endOffset))
                )
            } else {
                val angle = PI.toFloat() / 4f
                drawLine(
                    color = Color.LightGray,
                    start = center.plus(
                        Offset(x = cos(angle) * startOffset, y = -sin(angle) * startOffset)
                    ),
                    end =  center.plus(
                        Offset(x = cos(angle) * endOffset, y = -sin(angle) * endOffset)
                    )
                )
            }
        }
        if (!busyForks.contains(2)) {
            if (philosophers.size == 3) {
                drawLine(
                    color = Color.LightGray,
                    start = center.plus(Offset(x = .0f, y = startOffset)),
                    end =  center.plus(Offset(x = .0f, y = endOffset))
                )
            } else if (philosophers.size == 4) {
                val angle = 3f * PI.toFloat() / 4f
                drawLine(
                    color = Color.LightGray,
                    start = center.minus(Offset(x = cos(angle) * startOffset, y = -sin(angle) * startOffset)),
                    end =  center.minus(Offset(x = cos(angle) * endOffset, y = -sin(angle) * endOffset))
                )
            }
        }
        if (!busyForks.contains(3)) {
            if (philosophers.size == 4) {
                val angle = PI.toFloat() / 4f
                drawLine(
                    color = Color.LightGray,
                    start = center.minus(
                        Offset(x = cos(angle) * startOffset, y = -sin(angle) * startOffset)
                    ),
                    end =  center.minus(
                        Offset(x = cos(angle) * endOffset, y = -sin(angle) * endOffset)
                    )
                )
            }
        }


        philosophers.calculateCells(size, center, tableRadiusPercent)
            .forEachIndexed { index, cells ->
                val newMap = cellsMap + Pair(index, cells)
                cellsMap = newMap
            }


//        println("[DRAW CELLS]")
        // draw cells
//        println(cellsMap)
        cellsMap.flatMap { it.value }
            .forEach { cell ->
                drawCircle(
                    color = Color.Black,
                    radius = cell.radius,
                    center = cell.center,
                    style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
                )
                drawText(
                    textMeasurer = textMeasure,
                    text = "${cell.index}",
                    style = TextStyle(color = Color.White),
                    topLeft = cell.center.let { pos ->
                        when (cell.position) {
                            Position.LEFT -> pos.plus(Offset(-8f, cell.radius + 8))
                            Position.TOP -> pos.plus(Offset(cell.radius + 8, -8f))
                            Position.RIGHT -> pos.plus(Offset(-8f, cell.radius))
                            Position.BOTTOM -> pos.plus(Offset(cell.radius + 8, -8f))
                        }
                    }
                )
            }

        println(philosophersPositions)

//        println("[DRAW PHILS]")
        philosophers.zip(philosophersPositions).forEachIndexed { index, (philosopher , position) ->
            val cells = cellsMap[index] ?: emptyList()
            val activeCell: Cell? = cells.firstOrNull { it.index == position }
            activeCell?.also { cell ->
                drawCircle(
                    color = Color.Black,
                    radius = cell.radius * 0.8f,
                    center = cell.center,
                    style = Fill
                )
            }
        }
    }
}

private fun List<PhilosophersParallelGame.Philosopher>.calculateCells(
    size: Size,
    center: Offset,
    tableRadiusPercent: Float,
): List<List<Cell>> {
    return mapIndexed { index, philosopher ->
        val stepCount = philosopher.maxPosition.toFloat()
        val oneStepWidthPercent: Float = ((0.5f - tableRadiusPercent) / stepCount)
        val cellSize = oneStepWidthPercent * size.width
        var position: Position = Position.LEFT
        val coordinates: List<Offset> = (0 until stepCount.toInt()).mapNotNull {
            when (this.size) {
                2 -> when (index) {
                    0 -> center.copy(x = 0f).plus(
                        Offset(
                            x = cellSize / 2 + it * (2 * cellSize / 2), y = 0f
                        )
                    ).also { position = Position.LEFT }

                    1 -> center.copy(x = size.width).minus(
                        Offset(
                            x = cellSize / 2 + it * (2 * cellSize / 2), y = 0f
                        )
                    ).also { position = Position.RIGHT }

                    else -> null
                }

                3 -> when (index) {
                    0 -> center.copy(x = 0f).plus(
                        Offset(
                            x = cellSize / 2 + it * (2 * cellSize / 2), y = 0f
                        )
                    ).also { position = Position.LEFT }

                    1 -> center.copy(y = 0f).plus(
                        Offset(
                            x = 0f, y = cellSize / 2 + it * (2 * cellSize / 2)
                        )
                    ).also { position = Position.TOP }

                    2 -> center.copy(x = size.width).minus(
                        Offset(
                            x = cellSize / 2 + it * (2 * cellSize / 2), y = 0f
                        )
                    ).also { position = Position.RIGHT }

                    else -> null
                }

                4 -> when (index) {
                    0 -> center.copy(x = 0f).plus(
                        Offset(
                            x = cellSize / 2 + it * (2 * cellSize / 2), y = 0f
                        )
                    ).also { position = Position.LEFT }

                    1 -> center.copy(y = 0f).plus(
                        Offset(
                            x = 0f, y = cellSize / 2 + it * (2 * cellSize / 2)
                        )
                    ).also { position = Position.TOP }

                    2 -> center.copy(x = size.width).minus(
                        Offset(
                            x = cellSize / 2 + it * (2 * cellSize / 2), y = 0f
                        )
                    ).also { position = Position.RIGHT }

                    3 -> center.copy(y = size.width).minus(
                        Offset(
                            x = 0f, y = cellSize / 2 + it * (2 * cellSize / 2)
                        )
                    ).also { position = Position.BOTTOM }

                    else -> null
                }

                else -> null
            }
        }

        val cells = coordinates.mapIndexed { index, coordinate ->
            Cell(index, position, coordinate, cellSize)
        }
        cells
    }
}
