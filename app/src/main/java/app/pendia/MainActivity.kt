package app.pendia

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import app.pendia.ui.PendiaRoot
import app.pendia.ui.theme.PendiaTheme

/** AppCompatActivity, damit die Sprache in der App umgestellt werden kann. */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            PendiaTheme {
                PendiaRoot(repo)
            }
        }
    }
}
