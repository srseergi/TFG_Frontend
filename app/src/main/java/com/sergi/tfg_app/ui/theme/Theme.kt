package com.sergi.tfg_app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AmberPrimary,
    onPrimary = Stone950,
    primaryContainer = AmberContainer,
    onPrimaryContainer = OnAmberContainer,

    secondary = OrangePrimary,
    onSecondary = White,
    secondaryContainer = OrangeLight,
    onSecondaryContainer = OrangeDark,

    tertiary = AmberDark,
    onTertiary = White,
    tertiaryContainer = AmberLight,
    onTertiaryContainer = AmberDark,

    background = Stone50,
    onBackground = Stone700,
    surface = White,
    onSurface = Stone700,
    surfaceVariant = Stone100,
    onSurfaceVariant = Stone500,

    outline = Stone300,
    outlineVariant = Stone200,

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRedLight,
    onErrorContainer = ErrorRedDark
)

private val DarkColorScheme = darkColorScheme(
    primary = AmberPrimary,
    onPrimary = Stone950,
    primaryContainer = AmberDark,
    onPrimaryContainer = AmberLight,

    secondary = OrangePrimary,
    onSecondary = Stone950,
    secondaryContainer = OrangeDark,
    onSecondaryContainer = OrangeLight,

    tertiary = AmberLight,
    onTertiary = Stone950,
    tertiaryContainer = AmberDark,
    onTertiaryContainer = AmberLight,

    background = Stone950,
    onBackground = Stone100,
    surface = Stone900,
    onSurface = Stone100,
    surfaceVariant = Stone800,
    onSurfaceVariant = Stone400,

    outline = Stone600,
    outlineVariant = Stone700,

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRedDark,
    onErrorContainer = ErrorRedLight
)

@Composable
fun TFG_AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
