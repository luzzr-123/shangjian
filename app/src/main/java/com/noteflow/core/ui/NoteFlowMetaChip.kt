package com.luuzr.jielv.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import com.luuzr.jielv.core.designsystem.theme.NoteFlowGlassBorderSoft
import com.luuzr.jielv.core.designsystem.theme.NoteFlowGlassSurface

@Composable
fun NoteFlowMetaChip(
    text: String,
    modifier: Modifier = Modifier,
    accentColor: Color? = null,
) {
    val containerColor = accentColor?.copy(alpha = 0.14f) ?: NoteFlowGlassSurface.copy(alpha = 0.72f)
    val contentColor = accentColor ?: MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(
            width = 1.dp,
            color = (accentColor ?: NoteFlowGlassBorderSoft).copy(alpha = 0.24f),
        ),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
