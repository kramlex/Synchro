package ru.nsu.synchro.app.gui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.map
import ru.nsu.synchro.app.machine.Executor
import ru.nsu.synchro.app.phils.Philosopher
import ru.nsu.synchro.app.phils.PhilosopherGame
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class Position {
    LEFT, TOP, RIGHT, BOTTOM
}

data class Cell(
    val index: Int,
    val position: Position,
    val center: Offset,
    val size: Float,
) {
    val radius: Float get() = size / 2
}

@Composable
fun PhilosophersGame(game: PhilosopherGame) {
    val scrollState   = rememberScrollState()

    Row {
        Column(modifier = Modifier.padding(4.dp)) {
            PhilosophersBoard(
                game = game
            )
        }
        Column (
            modifier = Modifier
                .scrollable(scrollState, orientation = Orientation.Vertical)
        ) {
            Column {
                TextButton(
                    onClick = {
                        game.reset()
                    },
                ) {
                    Text("Перезапустить")
                }
                Divider()
                PhilosophersInfo(game)
            }
        }
    }
}

@Composable
fun PhilosophersInfo(game: PhilosopherGame) {
    val scope = rememberCoroutineScope()

    val philosopherAndExecutor: List<Pair<Executor, Philosopher>> by game.philosopherAndExecutor.collectAsState(
        initial = emptyList(),
        context = scope.coroutineContext
    )
    val states by game.states.collectAsState()



    philosopherAndExecutor.forEach { philosopher ->
        PhilosopherInfo(philosopher)
        Spacer(modifier = Modifier.height(12.dp))
    }.also { states }
}

@Composable
fun PhilosopherInfo(
    philosopherAndExecutor: Pair<Executor, Philosopher>,
) {
    val philosopher = philosopherAndExecutor.second
    val executor: Executor = philosopherAndExecutor.first

    val isRunning: Boolean by executor.isRunning.collectAsState()

    Column {
        Row {
            Column {
                Text("Философ #${philosopher.number}")
                Text("Состояние: ${philosopher.state.value.localizedString}")
            }
            TextButton(
                onClick = {
                    executor.start()
                },
                enabled = !isRunning
            ) {
                Text("Старт")
            }
            TextButton(
                onClick = {
                    executor.stop()
                },
                enabled = isRunning
            ) {
                Text("Стоп")
            }
        }

        Row {
            TextButton(
                onClick = {
                    philosopher.stepToTable()
                },
                enabled = philosopher.stepToTableIsValid()
            ) {
                Text("Шаг с столу")
            }

            TextButton(
                onClick = {
                    philosopher.stepFromTable()
                },
                enabled = philosopher.stepFromTableIsValid()
            ) {
                Text("Шаг от стола")
            }

            TextButton(
                onClick = {
                    philosopher.startEating()
                },
                enabled = philosopher.startEatingIsValid()
            ) {
                Text("Начать есть")
            }

            TextButton(
                onClick = {
                    philosopher.endEating()
                },
                enabled = philosopher.endEatingIsValid()
            ) {
                Text("Закончить есть")
            }
        }

        Row {
            TextButton(
                onClick = {
                    philosopher.takeRightFork()
                },
                enabled = philosopher.canTakeRightFork()
            ) {
                Text("Взять правую вилку")
            }

            TextButton(
                onClick = {
                    philosopher.takeLeftFork()
                },
                enabled = philosopher.canTakeLeftFork()
            ) {
                Text("Взять левую вилку")
            }
        }

        Row {
            TextButton(
                onClick = {
                    philosopher.putBackRightFork()
                },
                enabled = philosopher.putBackRightForkIsValid()
            ) {
                Text("Положить правую вилку")
            }

            TextButton(
                onClick = {
                    philosopher.putBackLeftFork()
                },
                enabled = philosopher.putBackLeftForkIsValid()
            ) {
                Text("Положить левую вилку")
            }
        }

        Divider()
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun PhilosophersBoard(
    game: PhilosopherGame
) {
    val scope = rememberCoroutineScope()

    val philosophers by game.philosopherAndExecutor
        .map { list -> list.map { it.second } }
        .collectAsState(initial = emptyList())

    val states: Map<Int, Philosopher.State> by game.states.collectAsState(
        initial = emptyMap(),
        context = scope.coroutineContext
    )

    val forks: Map<Int, Int> by game.forks.collectAsState(
        initial = emptyMap(),
        context = scope.coroutineContext
    )

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

        if (forks[1] == null) {
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
        if (forks[2] == null) {
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
        if (forks[3] == null) {
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
        if (forks[4] == null) {
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
            .forEach { (philosopher, cells) ->
                val newMap = cellsMap + Pair(philosopher.number, cells)
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

//        println("[DRAW PHILS]")
        states.forEach { (number , state) ->
            val cells = cellsMap[number] ?: emptyList()
            val activeCell: Cell? = when (state) {
                is Philosopher.State.Step -> cells.firstOrNull { it.index == state.stepNumber }
                else -> cells.maxByOrNull { it.index }
            }
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

fun List<Philosopher>.calculateCells(
    size: Size,
    center: Offset,
    tableRadiusPercent: Float,
): List<Pair<Philosopher, List<Cell>>> {
    return map { philosopher ->
        val stepCount = philosopher.stepCount.toFloat()
        val oneStepWidthPercent: Float = ((0.5f - tableRadiusPercent) / stepCount)
        val cellSize = oneStepWidthPercent * size.width
        var position: Position = Position.LEFT
        val coordinates: List<Offset> = (0 until stepCount.toInt()).mapNotNull {
            when (this.size) {
                2 -> when (philosopher.number) {
                    1 -> center.copy(x = 0f).plus(
                        Offset(
                            x = cellSize / 2 + it * (2 * cellSize / 2), y = 0f
                        )
                    ).also { position = Position.LEFT }

                    2 -> center.copy(x = size.width).minus(
                        Offset(
                            x = cellSize / 2 + it * (2 * cellSize / 2), y = 0f
                        )
                    ).also { position = Position.RIGHT }

                    else -> null
                }

                3 -> when (philosopher.number) {
                    1 -> center.copy(x = 0f).plus(
                        Offset(
                            x = cellSize / 2 + it * (2 * cellSize / 2), y = 0f
                        )
                    ).also { position = Position.LEFT }

                    2 -> center.copy(y = 0f).plus(
                        Offset(
                            x = 0f, y = cellSize / 2 + it * (2 * cellSize / 2)
                        )
                    ).also { position = Position.TOP }

                    3 -> center.copy(x = size.width).minus(
                        Offset(
                            x = cellSize / 2 + it * (2 * cellSize / 2), y = 0f
                        )
                    ).also { position = Position.RIGHT }

                    else -> null
                }

                4 -> when (philosopher.number) {
                    1 -> center.copy(x = 0f).plus(
                        Offset(
                            x = cellSize / 2 + it * (2 * cellSize / 2), y = 0f
                        )
                    ).also { position = Position.LEFT }

                    2 -> center.copy(y = 0f).plus(
                        Offset(
                            x = 0f, y = cellSize / 2 + it * (2 * cellSize / 2)
                        )
                    ).also { position = Position.TOP }

                    3 -> center.copy(x = size.width).minus(
                        Offset(
                            x = cellSize / 2 + it * (2 * cellSize / 2), y = 0f
                        )
                    ).also { position = Position.RIGHT }

                    4 -> center.copy(y = size.width).minus(
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
        Pair(philosopher, cells)
    }
}
