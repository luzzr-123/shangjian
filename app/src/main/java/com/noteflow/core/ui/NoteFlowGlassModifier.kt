package com.luuzr.jielv.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.luuzr.jielv.core.designsystem.theme.NoteFlowGlassBorder
import com.luuzr.jielv.core.designsystem.theme.NoteFlowGlassBorderSoft
import com.luuzr.jielv.core.designsystem.theme.NoteFlowBackgroundRaised
import com.luuzr.jielv.core.designsystem.theme.NoteFlowSurface
import com.luuzr.jielv.core.designsystem.theme.NoteFlowSurfaceVariant

fun Modifier.noteFlowGlassBackground(
    level: GlassLevel = GlassLevel.Normal,
    accentColor: Color = Color.Transparent,
    shape: Shape = RoundedCornerShape(24.dp),
): Modifier {
    val containerColor = when (level) {
        GlassLevel.Weak -> NoteFlowBackgroundRaised
        GlassLevel.Normal -> NoteFlowSurface
        GlassLevel.Strong -> NoteFlowSurfaceVariant
    }
    val borderColor = when (level) {
        GlassLevel.Weak -> NoteFlowGlassBorderSoft.copy(alpha = 0.70f)
        GlassLevel.Normal -> NoteFlowGlassBorderSoft
        GlassLevel.Strong -> NoteFlowGlassBorder
    }
    val resolvedBorderColor = if (accentColor != Color.Transparent) {
        lerp(
            borderColor,
            accentColor.copy(alpha = borderColor.alpha),
            0.22f,
        )
    } else {
        borderColor
    }

    return this
        .clip(shape)
        .background(containerColor)
        .border(width = 1.dp, color = resolvedBorderColor, shape = shape)
}
