package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class ArenaTheme(
    val displayName: String,
    val primary: Color,
    val primaryDark: Color,
    val background: Color,
    val surface: Color,
    val accent: Color,
    val onPrimary: Color = Color.White
) {
    RED(
        displayName = "Crimson Red",
        primary = Color(0xFFD32F2F),
        primaryDark = Color(0xFFC62828),
        background = Color(0xFFFFF0F5),
        surface = Color(0xFFFFFFFF),
        accent = Color(0xFFFF5252)
    ),
    EMERALD(
        displayName = "Emerald Green",
        primary = Color(0xFF2E7D32),
        primaryDark = Color(0xFF1B5E20),
        background = Color(0xFFE8F5E9),
        surface = Color(0xFFFFFFFF),
        accent = Color(0xFF4CAF50)
    ),
    AZURE(
        displayName = "Azure Blue",
        primary = Color(0xFF1565C0),
        primaryDark = Color(0xFF0D47A1),
        background = Color(0xFFE3F2FD),
        surface = Color(0xFFFFFFFF),
        accent = Color(0xFF42A5F5)
    ),
    RUST(
        displayName = "Rust Orange",
        primary = Color(0xFFD84315),
        primaryDark = Color(0xFFBF360C),
        background = Color(0xFFFBE9E7),
        surface = Color(0xFFFFFFFF),
        accent = Color(0xFFFF7043)
    ),
    YELLOW(
        displayName = "Solar Yellow",
        primary = Color(0xFFE65100),
        primaryDark = Color(0xFFBF360C),
        background = Color(0xFFFFFDE7),
        surface = Color(0xFFFFFFFF),
        accent = Color(0xFFFFB300)
    ),
    PINK(
        displayName = "Lotus Pink",
        primary = Color(0xFFC2185B),
        primaryDark = Color(0xFF880E4F),
        background = Color(0xFFFCE4EC),
        surface = Color(0xFFFFFFFF),
        accent = Color(0xFFFF4081)
    ),
    DARK_LAVENDER(
        displayName = "Dark Lavender",
        primary = Color(0xFF512DA8),
        primaryDark = Color(0xFF311B92),
        background = Color(0xFFEDE7F6),
        surface = Color(0xFFFFFFFF),
        accent = Color(0xFF7E57C2)
    );

    val headerGradient: Brush
        get() = Brush.verticalGradient(
            colors = listOf(primary, primaryDark)
        )

    val cardBorder: Color
        get() = primary.copy(alpha = 0.15f)

    companion object {
        fun fromString(name: String): ArenaTheme {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: RED
        }
    }
}
