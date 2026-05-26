package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF4FC3F7),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFF81D4FA),
    onSecondary = Color(0xFF003353),
    tertiary = Color(0xFFB0BEC5),
    onTertiary = Color(0xFF1C2B32),
    tertiaryContainer = Color(0xFF324148),
    onTertiaryContainer = Color(0xFFCFE6F1),
    background = Color(0xFF101416),
    onBackground = Color(0xFFE1E2E4),
    surface = Color(0xFF101416),
    onSurface = Color(0xFFE1E2E4),
    surfaceVariant = Color(0xFF222B30),
    onSurfaceVariant = Color(0xFFBFC7D0)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF0277BD),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF0288D1),
    onSecondary = Color.White,
    background = Color(0xFFF8FDFF),
    onBackground = Color(0xFF101416),
    surface = Color(0xFFF8FDFF),
    onSurface = Color(0xFF101416),
    surfaceVariant = Color(0xFFDDE3EB),
    onSurfaceVariant = Color(0xFF41484D)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
