package ru.nsu.synchro

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.nsu.synchro.board.PhilosophersGame
import ru.nsu.synchro.phils.PhilosopherGame
import ru.nsu.synchro.theme.AppTheme

@Composable
internal fun App(
) = AppTheme {

    val game: PhilosopherGame = remember { PhilosopherGame(listOf(4,5,4)) }

    Box(modifier = Modifier.fillMaxSize()) {
        PhilosophersGame(game)
    }
}
