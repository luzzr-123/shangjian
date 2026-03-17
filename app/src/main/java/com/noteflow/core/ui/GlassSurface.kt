package com.luuzr.jielv.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.luuzr.jielv.core.designsystem.theme.NoteFlowGlassBorder
import com.luuzr.jielv.core.designsystem.theme.NoteFlowGlassBorderSoft
import com.luuzr.jielv.core.designsystem.theme.NoteFlowBackgroundRaised
import com.luuzr.jielv.core.designsystem.theme.NoteFlowSurface
import com.luuzr.jielv.core.designsystem.theme.NoteFlowSurfaceVariant

enum class GlassLevel {
    Weak,
    Normal,
    Strong,
}

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    accentColor: Color? = null,
    shape: Shape = MaterialTheme.shapes.large,
    level: GlassLevel = GlassLevel.Normal,
    strong: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val resolvedLevel = if (strong) GlassLevel.Strong else level
    val containerColor = when (resolvedLevel) {
        GlassLevel.Weak -> NoteFlowBackgroundRaised
        GlassLevel.Normal -> NoteFlowSurface
        GlassLevel.Strong -> NoteFlowSurfaceVariant
    }
    val borderColor = when (resolvedLevel) {
        GlassLevel.Weak -> NoteFlowGlassBorderSoft.copy(alpha = 0.68f)
        GlassLevel.Normal -> NoteFlowGlassBorderSoft
        GlassLevel.Strong -> NoteFlowGlassBorder
    }
    val shadowElevation = when (resolvedLevel) {
        GlassLevel.Weak -> 2.dp
        GlassLevel.Normal -> 4.dp
        GlassLevel.Strong -> 7.dp
    }

    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        shadowElevation = shadowElevation,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Box(content = content)
    }
}
