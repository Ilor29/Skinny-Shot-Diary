package app.pendia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Teal = Color(0xFF0F5E63)
val Coral = Color(0xFFF07B62)

private val Light = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3ECEA),
    onPrimaryContainer = Color(0xFF00363A),
    secondary = Color(0xFF4A6365),
    tertiary = Color(0xFFB4452F),
    tertiaryContainer = Color(0xFFFFDAD2),
    onTertiaryContainer = Color(0xFF3D0600),
    background = Color(0xFFF7F8F7),
    surface = Color.White,
    surfaceVariant = Color(0xFFE6ECEB),
    outlineVariant = Color(0xFFD5DCDB),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF3F6F5),
    surfaceContainer = Color(0xFFEDF2F1),
    surfaceContainerHigh = Color(0xFFE7EDEC),
    surfaceContainerHighest = Color(0xFFE1E8E7),
    secondaryContainer = Color(0xFFCDE8E6),
)

private val Dark = darkColorScheme(
    primary = Color(0xFF7FD0CF),
    onPrimary = Color(0xFF003739),
    primaryContainer = Color(0xFF0F4F53),
    onPrimaryContainer = Color(0xFFB3ECEA),
    secondary = Color(0xFFB0CBCB),
    tertiary = Color(0xFFFFB4A3),
    tertiaryContainer = Color(0xFF7A2A1A),
    onTertiaryContainer = Color(0xFFFFDAD2),
    background = Color(0xFF111414),
    surface = Color(0xFF181C1C),
    surfaceVariant = Color(0xFF2A3131),
    outlineVariant = Color(0xFF3B4444),
    surfaceContainerLowest = Color(0xFF0C0F0F),
    surfaceContainerLow = Color(0xFF161A1A),
    surfaceContainer = Color(0xFF1A1F1F),
    surfaceContainerHigh = Color(0xFF242A2A),
    surfaceContainerHighest = Color(0xFF2F3535),
    secondaryContainer = Color(0xFF1F4D50),
)

@Composable
fun PendiaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}
