package com.example.radiostreaming.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.radiostreaming.domain.model.ThemeMode

private val BlueColorScheme = darkColorScheme(
    primary = YellowPrimary,
    onPrimary = BlueDark,
    secondary = YellowPrimary,
    onSecondary = BlueDark,
    tertiary = YellowPrimary,
    background = BlueBackground,
    onBackground = GrayText,
    surface = BlueSurface,
    onSurface = GrayText,
    outline = YellowPrimary
)

private val YellowColorScheme = lightColorScheme(
    primary = BlueAccent,
    onPrimary = Color.White,
    secondary = BlueAccent,
    onSecondary = Color.White,
    tertiary = BlueAccent,
    background = YellowBackground,
    onBackground = BlueText,
    surface = YellowSurface,
    onSurface = BlueText,
    outline = BlueAccent
)

@Composable
fun RadiostreamingTheme(
    mode: ThemeMode = ThemeMode.BLUE,
    content: @Composable () -> Unit
) {
    val colorScheme = when (mode) {
        ThemeMode.BLUE -> BlueColorScheme
        ThemeMode.YELLOW -> YellowColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            // Con fondo amarillo, la hora y la batería se ven en oscuro
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                mode == ThemeMode.YELLOW
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}