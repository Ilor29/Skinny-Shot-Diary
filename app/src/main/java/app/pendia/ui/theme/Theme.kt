package app.pendia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Salbei-Palette. Alle Farben sind gesetzt, damit keine Material-Standardfarbe (Lila) durchscheint.
 * Beschreibung und Regeln: docs/design_v1.md
 */
val Sage = Color(0xFF2D6A4F)
val Coral = Color(0xFFF07B62)

private val Light = lightColorScheme(
    primary = Sage,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8F3DC),
    onPrimaryContainer = Color(0xFF081C15),
    inversePrimary = Color(0xFF52B788),
    secondary = Color(0xFF1A759F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCBEBD3),
    onSecondaryContainer = Color(0xFF0B2B1D),
    tertiary = Color(0xFFB4452F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDAD2),
    onTertiaryContainer = Color(0xFF3D0600),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF212529),
    surface = Color.White,
    onSurface = Color(0xFF212529),
    surfaceVariant = Color(0xFFE3EBE6),
    onSurfaceVariant = Color(0xFF4F5B55),
    surfaceTint = Sage,
    inverseSurface = Color(0xFF2E3230),
    inverseOnSurface = Color(0xFFEFF2F0),
    outline = Color(0xFF75817B),
    outlineVariant = Color(0xFFD3DBD6),
    surfaceBright = Color.White,
    surfaceDim = Color(0xFFD9DEDB),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF3F6F4),
    surfaceContainer = Color(0xFFEDF2EF),
    surfaceContainerHigh = Color(0xFFE7EDE9),
    surfaceContainerHighest = Color(0xFFE1E8E3),
)

private val Dark = darkColorScheme(
    primary = Color(0xFF52B788),
    onPrimary = Color(0xFF081C15),
    primaryContainer = Color(0xFF1B4332),
    onPrimaryContainer = Color(0xFFD8F3DC),
    inversePrimary = Sage,
    secondary = Color(0xFF34A0A4),
    onSecondary = Color(0xFF003543),
    secondaryContainer = Color(0xFF1F4A36),
    onSecondaryContainer = Color(0xFFCFEBD8),
    tertiary = Color(0xFFFFB4A3),
    onTertiary = Color(0xFF5F1405),
    tertiaryContainer = Color(0xFF7A2A1A),
    onTertiaryContainer = Color(0xFFFFDAD2),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE3E3E3),
    surface = Color(0xFF1A1C1B),
    onSurface = Color(0xFFE3E3E3),
    surfaceVariant = Color(0xFF2B312E),
    onSurfaceVariant = Color(0xFFBFC9C3),
    surfaceTint = Color(0xFF52B788),
    inverseSurface = Color(0xFFE3E3E3),
    inverseOnSurface = Color(0xFF2E3230),
    outline = Color(0xFF89938D),
    outlineVariant = Color(0xFF3C4440),
    surfaceBright = Color(0xFF383B39),
    surfaceDim = Color(0xFF121212),
    surfaceContainerLowest = Color(0xFF0E100F),
    surfaceContainerLow = Color(0xFF1A1C1B),
    surfaceContainer = Color(0xFF1E211F),
    surfaceContainerHigh = Color(0xFF282B29),
    surfaceContainerHighest = Color(0xFF333634),
)

@Composable
fun PendiaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) Dark else Light, content = content)
}
