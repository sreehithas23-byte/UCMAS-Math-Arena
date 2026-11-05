package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun UcmasArenaTheme(
    arenaTheme: ArenaTheme = ArenaTheme.RED,
    content: @Composable () -> Unit
) {
    val colorScheme = lightColorScheme(
        primary = arenaTheme.primary,
        onPrimary = arenaTheme.onPrimary,
        primaryContainer = arenaTheme.primary.copy(alpha = 0.12f),
        onPrimaryContainer = arenaTheme.primaryDark,
        secondary = arenaTheme.accent,
        onSecondary = Color.White,
        secondaryContainer = arenaTheme.accent.copy(alpha = 0.2f),
        onSecondaryContainer = arenaTheme.primaryDark,
        background = arenaTheme.background,
        onBackground = Color(0xFF1E1E24),
        surface = arenaTheme.surface,
        onSurface = Color(0xFF1E1E24),
        surfaceVariant = arenaTheme.background,
        onSurfaceVariant = Color(0xFF555566),
        outline = arenaTheme.primary.copy(alpha = 0.25f)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
