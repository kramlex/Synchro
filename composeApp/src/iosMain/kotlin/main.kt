import androidx.compose.ui.window.ComposeUIViewController
import ru.nsu.synchro.app.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController { App() }
}
