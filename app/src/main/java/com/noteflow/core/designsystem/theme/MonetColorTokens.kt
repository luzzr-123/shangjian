package com.luuzr.jielv.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class MonetModulePalette(
    val accent: Color,
    val accentSoft: Color,
    val accentGlow: Color,
)

object MonetColorTokens {
    val canvas = NoteFlowCanvasLayer
    val canvasRaised = NoteFlowCanvasLayerRaised
    val background = NoteFlowBackground
    val surface = NoteFlowSurface
    val surfaceFloating = NoteFlowSurfaceFloating
    val textPrimary = NoteFlowTextPrimary
    val textSecondary = NoteFlowTextSecondary
    val textTertiary = NoteFlowTextTertiary

    val today = MonetModulePalette(
        accent = NoteFlowTodayAccent,
        accentSoft = NoteFlowTodayAccentSoft,
        accentGlow = NoteFlowTodayAccentGlow,
    )
    val task = MonetModulePalette(
        accent = NoteFlowTaskAccent,
        accentSoft = NoteFlowTaskAccentSoft,
        accentGlow = NoteFlowTaskAccentGlow,
    )
    val habit = MonetModulePalette(
        accent = NoteFlowHabitAccent,
        accentSoft = NoteFlowHabitAccentSoft,
        accentGlow = NoteFlowHabitAccentGlow,
    )
    val note = MonetModulePalette(
        accent = NoteFlowNoteAccent,
        accentSoft = NoteFlowNoteAccentSoft,
        accentGlow = NoteFlowNoteAccentGlow,
    )
}
