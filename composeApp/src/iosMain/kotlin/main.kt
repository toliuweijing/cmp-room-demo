import androidx.compose.ui.window.ComposeUIViewController
import org.example.project.App
import org.example.project.data.local.DatabaseDriverFactory

fun MainViewController() = ComposeUIViewController { App(driverFactory = DatabaseDriverFactory()) }