import androidx.compose.ui.window.ComposeUIViewController
import ru.nsu.synchro.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController { App() }
}
