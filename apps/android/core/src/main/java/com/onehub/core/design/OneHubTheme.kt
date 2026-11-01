package com.onehub.core.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Provisional palette — final brand identity not locked. */
object OneHubPalette {
    val HubBlue = Color(0xFF1B4F9C)
    val HubBlueLight = Color(0xFF4A7FD4)
    val HubTeal = Color(0xFF0D9488)
    val HubSurface = Color(0xFFF4F6FA)
    val HubSurfaceDark = Color(0xFF0F1419)
}

private val LightColors = lightColorScheme(
    primary = OneHubPalette.HubBlue,
    secondary = OneHubPalette.HubTeal,
    tertiary = OneHubPalette.HubBlueLight,
    background = OneHubPalette.HubSurface,
    surface = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = OneHubPalette.HubBlueLight,
    secondary = OneHubPalette.HubTeal,
    tertiary = OneHubPalette.HubBlue,
    background = OneHubPalette.HubSurfaceDark,
    surface = Color(0xFF1A222D),
)

@Composable
fun OneHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
