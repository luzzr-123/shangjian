package com.luuzr.jielv.core.ui

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.SwitchColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.luuzr.jielv.core.designsystem.theme.NoteFlowBackgroundRaised
import com.luuzr.jielv.core.designsystem.theme.NoteFlowOutlineSoft
import com.luuzr.jielv.core.designsystem.theme.NoteFlowOnAccent
import com.luuzr.jielv.core.designsystem.theme.NoteFlowSurfaceVariant

@Composable
fun noteFlowSwitchColors(
    accentColor: Color = MaterialTheme.colorScheme.primary,
): SwitchColors = SwitchDefaults.colors(
    checkedThumbColor = NoteFlowBackgroundRaised,
    checkedTrackColor = accentColor.copy(alpha = 0.82f),
    checkedBorderColor = accentColor.copy(alpha = 0.42f),
    checkedIconColor = accentColor,
    uncheckedThumbColor = NoteFlowBackgroundRaised,
    uncheckedTrackColor = NoteFlowSurfaceVariant.copy(alpha = 0.64f),
    uncheckedBorderColor = NoteFlowOutlineSoft.copy(alpha = 0.68f),
    uncheckedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.82f),
    disabledCheckedThumbColor = NoteFlowBackgroundRaised.copy(alpha = 0.64f),
    disabledCheckedTrackColor = accentColor.copy(alpha = 0.42f),
    disabledCheckedBorderColor = accentColor.copy(alpha = 0.30f),
    disabledCheckedIconColor = accentColor.copy(alpha = 0.64f),
    disabledUncheckedThumbColor = NoteFlowBackgroundRaised.copy(alpha = 0.64f),
    disabledUncheckedTrackColor = NoteFlowSurfaceVariant.copy(alpha = 0.46f),
    disabledUncheckedBorderColor = NoteFlowOutlineSoft.copy(alpha = 0.42f),
    disabledUncheckedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.54f),
)

@Composable
fun noteFlowButtonColors(
    accentColor: Color = MaterialTheme.colorScheme.primary,
): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = accentColor,
    contentColor = MaterialTheme.colorScheme.onPrimary,
    disabledContainerColor = accentColor.copy(alpha = 0.44f),
    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.62f),
)

@Composable
fun noteFlowOutlinedButtonColors(): ButtonColors = ButtonDefaults.outlinedButtonColors(
    containerColor = NoteFlowBackgroundRaised,
    contentColor = MaterialTheme.colorScheme.onSurface,
    disabledContainerColor = NoteFlowSurfaceVariant.copy(alpha = 0.8f),
    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.62f),
)

@Composable
fun noteFlowFilterChipColors(
    accentColor: Color = MaterialTheme.colorScheme.primary,
): SelectableChipColors = FilterChipDefaults.filterChipColors(
    selectedContainerColor = accentColor.copy(alpha = 0.18f),
    selectedLabelColor = MaterialTheme.colorScheme.onSurface,
    selectedLeadingIconColor = accentColor,
    selectedTrailingIconColor = accentColor,
    containerColor = NoteFlowBackgroundRaised,
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledContainerColor = NoteFlowSurfaceVariant.copy(alpha = 0.8f),
    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.58f),
)

@Composable
fun noteFlowCheckboxColors(
    accentColor: Color = MaterialTheme.colorScheme.primary,
): CheckboxColors = CheckboxDefaults.colors(
    checkedColor = accentColor.copy(alpha = 0.86f),
    checkmarkColor = NoteFlowOnAccent,
    uncheckedColor = NoteFlowSurfaceVariant.copy(alpha = 0.72f),
    disabledCheckedColor = accentColor.copy(alpha = 0.42f),
    disabledUncheckedColor = NoteFlowSurfaceVariant.copy(alpha = 0.36f),
)
