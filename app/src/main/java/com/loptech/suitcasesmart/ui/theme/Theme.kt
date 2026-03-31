package com.loptech.suitcasesmart.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AviationNavyLight,          // Sky blue legible sobre fondos oscuros
    onPrimary = Color(0xFFFFFFFF),
    secondary = MainAccent,               // Coral para CTAs secundarios
    onSecondary = Color(0xFFFFFFFF),
    tertiary = MainColor,                 // Teal como acento menor
    background = Color(0xFF111318),       // Fondo oscuro con tinte azul
    onBackground = Color(0xFFE3E3EC),
    surface = Color(0xFF1A1B22),          // Superficie ligeramente más clara
    onSurface = Color(0xFFE3E3EC),
    surfaceVariant = Color(0xFF252630),
    onSurfaceVariant = Color(0xFFB0B0C0),
)

private val LightColorScheme = lightColorScheme(
    primary = AviationNavy,              // Navy oscuro: botones, iconos activos
    onPrimary = Color(0xFFFFFFFF),
    secondary = MainAccent,              // Coral para destacados
    onSecondary = Color(0xFFFFFFFF),
    tertiary = MainColor,                // Teal como acento menor
    background = Color(0xFFF4F5F9),      // Fondo gris azulado muy suave
    onBackground = Color(0xFF1A1A2E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFFEEEFF5),
    onSurfaceVariant = Color(0xFF4A4A6A),
)

@Composable
fun SuitcaseSmartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
