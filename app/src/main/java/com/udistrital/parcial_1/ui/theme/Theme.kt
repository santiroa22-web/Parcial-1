package com.udistrital.parcial_1.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DetectiveAccentBlue,
    secondary = DetectiveAccentCyan,
    tertiary = DetectiveGold,
    background = DetectiveDarkBg,
    surface = DetectiveCardBg,
    onPrimary = DetectiveTextPrimary,
    onSecondary = DetectiveTextPrimary,
    onBackground = DetectiveTextPrimary,
    onSurface = DetectiveTextPrimary
)

@Composable
fun Parcial1Theme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}