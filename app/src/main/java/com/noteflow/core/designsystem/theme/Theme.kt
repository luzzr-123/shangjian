package com.luuzr.jielv.core.designsystem.theme

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

private val LightColorScheme = lightColorScheme(
    primary = NoteFlowTodayAccent,
    onPrimary = NoteFlowOnAccent,
    secondary = NoteFlowTaskAccent,
    tertiary = NoteFlowHabitAccent,
    background = NoteFlowBackground,
    onBackground = NoteFlowTextPrimary,
    surface = NoteFlowSurface,
    onSurface = NoteFlowTextPrimary,
    surfaceVariant = NoteFlowSurfaceVariant,
    onSurfaceVariant = NoteFlowTextSecondary,
    outline = NoteFlowOutlineSoft,
    outlineVariant = NoteFlowOutlineSoft.copy(alpha = 0.72f),
    primaryContainer = NoteFlowTodayAccentSoft,
    secondaryContainer = NoteFlowTaskAccentSoft,
    tertiaryContainer = NoteFlowHabitAccentSoft,
    error = NoteFlowDanger,
)

private val DarkColorScheme = darkColorScheme(
    primary = NoteFlowTaskAccent,
    onPrimary = Color(0xFF201E1A),
    secondary = NoteFlowHabitAccent,
    tertiary = NoteFlowNoteAccent,
    background = Color(0xFF161514),
    onBackground = Color(0xFFF3EFE8),
    surface = Color(0xFF1D1B19),
    onSurface = Color(0xFFF3EFE8),
    surfaceVariant = Color(0xFF2A2724),
    onSurfaceVariant = Color(0xFFD0C8BD),
    outline = Color(0xFF4A443D),
    outlineVariant = Color(0xFF3C3732),
    primaryContainer = Color(0xFF37315A),
    secondaryContainer = Color(0xFF2D433B),
    tertiaryContainer = Color(0xFF4A4126),
    error = Color(0xFFE08B8B),
)

@Composable
fun NoteFlowTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && useDarkTheme -> {
            dynamicDarkColorScheme(LocalContext.current)
        }
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicLightColorScheme(LocalContext.current)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NoteFlowTypography,
        shapes = NoteFlowShapes,
        content = content,
    )
}
