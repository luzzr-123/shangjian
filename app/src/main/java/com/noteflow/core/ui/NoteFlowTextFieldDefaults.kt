package com.luuzr.jielv.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import com.luuzr.jielv.core.designsystem.theme.NoteFlowBackgroundRaised
import com.luuzr.jielv.core.designsystem.theme.NoteFlowSurfaceVariant
import com.luuzr.jielv.core.designsystem.theme.NoteFlowOutlineSoft
import com.luuzr.jielv.core.designsystem.theme.NoteFlowTextPrimary
import com.luuzr.jielv.core.designsystem.theme.NoteFlowTextSecondary

@Composable
fun noteFlowOutlinedTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedContainerColor = NoteFlowBackgroundRaised,
        unfocusedContainerColor = NoteFlowBackgroundRaised,
        disabledContainerColor = NoteFlowSurfaceVariant.copy(alpha = 0.8f),
        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.42f),
        unfocusedBorderColor = NoteFlowOutlineSoft.copy(alpha = 0.74f),
        disabledBorderColor = NoteFlowOutlineSoft.copy(alpha = 0.36f),
        focusedTextColor = NoteFlowTextPrimary,
        unfocusedTextColor = NoteFlowTextPrimary,
        focusedLabelColor = NoteFlowTextSecondary,
        unfocusedLabelColor = NoteFlowTextSecondary,
        focusedPlaceholderColor = NoteFlowTextSecondary.copy(alpha = 0.82f),
        unfocusedPlaceholderColor = NoteFlowTextSecondary.copy(alpha = 0.74f),
        focusedSupportingTextColor = NoteFlowTextSecondary,
        unfocusedSupportingTextColor = NoteFlowTextSecondary,
        cursorColor = MaterialTheme.colorScheme.primary,
    )
}
