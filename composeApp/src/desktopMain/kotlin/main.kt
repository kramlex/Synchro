import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ru.nsu.synchro.app.App

fun main() = application {

    val windowState: WindowState = rememberWindowState(width = 800.dp, height = 600.dp)

    Window(
        title = "SynchroGame",
        state = windowState,
        onCloseRequest = ::exitApplication,
    ) {
        App()
    }
}
