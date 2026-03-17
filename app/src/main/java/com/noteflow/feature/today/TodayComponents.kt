package com.luuzr.jielv.feature.today

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.luuzr.jielv.core.designsystem.theme.NoteFlowHabitAccent
import com.luuzr.jielv.core.designsystem.theme.NoteFlowHabitAccentSoft
import com.luuzr.jielv.core.designsystem.theme.NoteFlowTaskAccent
import com.luuzr.jielv.core.designsystem.theme.NoteFlowTaskAccentSoft
import com.luuzr.jielv.core.designsystem.theme.NoteFlowTextSecondary
import com.luuzr.jielv.core.designsystem.theme.NoteFlowTodayAccent
import com.luuzr.jielv.core.ui.GlassLevel
import com.luuzr.jielv.core.ui.GlassSurface
import com.luuzr.jielv.core.ui.LocalRadialExpansionController
import com.luuzr.jielv.core.ui.ModuleFab
import com.luuzr.jielv.core.ui.MotionTokens
import com.luuzr.jielv.core.ui.noteFlowButtonColors
import com.luuzr.jielv.core.ui.noteFlowOutlinedButtonColors
import com.luuzr.jielv.core.ui.noteFlowPressScale
import com.luuzr.jielv.core.ui.rememberPressInteractionSource
import com.luuzr.jielv.domain.usecase.HabitQuickActionType
import com.luuzr.jielv.domain.usecase.TaskQuickActionType

internal data class TodayCompactLayoutSpec(
    val columnGap: Dp,
    val sectionGap: Dp,
    val cardGap: Dp,
    val cardPadding: Dp,
    val cardHeight: Dp,
    val emptyCardMinHeight: Dp,
    val controlHeight: Dp,
    val actionWidth: Dp,
    val sectionTitleStyle: TextStyle,
    val cardTitleStyle: TextStyle,
    val actionStyle: TextStyle,
    val supportStyle: TextStyle,
)

@Composable
internal fun rememberTodayCompactLayoutSpec(totalWidth: Dp): TodayCompactLayoutSpec {
    val typography = MaterialTheme.typography
    val dense = totalWidth < 360.dp
    return if (dense) {
        TodayCompactLayoutSpec(
            columnGap = 10.dp,
            sectionGap = 8.dp,
            cardGap = 8.dp,
            cardPadding = 12.dp,
            cardHeight = 88.dp,
            emptyCardMinHeight = 160.dp,
            controlHeight = 30.dp,
            actionWidth = 62.dp,
            sectionTitleStyle = typography.titleSmall,
            cardTitleStyle = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            actionStyle = typography.labelSmall,
            supportStyle = typography.bodySmall,
        )
    } else {
        TodayCompactLayoutSpec(
            columnGap = 12.dp,
            sectionGap = 10.dp,
            cardGap = 10.dp,
            cardPadding = 14.dp,
            cardHeight = 96.dp,
            emptyCardMinHeight = 176.dp,
            controlHeight = 32.dp,
            actionWidth = 68.dp,
            sectionTitleStyle = typography.titleMedium,
            cardTitleStyle = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            actionStyle = typography.labelMedium,
            supportStyle = typography.bodySmall,
        )
    }
}

@Composable
fun TodaySummaryCard(
    summary: TodaySummaryUiModel,
    modifier: Modifier = Modifier,
) {
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("today_summary_card"),
        accentColor = NoteFlowTodayAccent,
        level = GlassLevel.Weak,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "今日概览",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("today_summary_grid"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TodayMetricCell(
                    modifier = Modifier.weight(1f),
                    label = "待办",
                    value = summary.pendingTaskCount.toString(),
                    accentColor = NoteFlowTaskAccent,
                    testTag = "today_summary_pending_tasks",
                )
                TodayMetricCell(
                    modifier = Modifier.weight(1f),
                    label = "习惯",
                    value = summary.dueHabitCount.toString(),
                    accentColor = NoteFlowHabitAccent,
                    testTag = "today_summary_due_habits",
                )
                TodayMetricCell(
                    modifier = Modifier.weight(1f),
                    label = "已完成",
                    value = summary.completedCount.toString(),
                    accentColor = NoteFlowTodayAccent,
                    testTag = "today_summary_completed",
                )
            }
        }
    }
}

@Composable
private fun TodayMetricCell(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    accentColor: Color,
    testTag: String,
) {
    Column(
        modifier = modifier
            .heightIn(min = 60.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = accentColor,
            maxLines = 1,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = NoteFlowTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun TodaySectionHeader(
    title: String,
    actionLabel: String,
    testTag: String,
    layoutSpec: TodayCompactLayoutSpec,
    onActionClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = layoutSpec.sectionTitleStyle,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        TextButton(
            modifier = Modifier
                .defaultMinSize(minWidth = 0.dp, minHeight = 0.dp)
                .testTag(testTag),
            onClick = onActionClick,
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
        ) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
            )
        }
    }
}

@Composable
internal fun TodayTaskCard(
    item: TodayTaskCardUiModel,
    layoutSpec: TodayCompactLayoutSpec,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onAction: () -> Unit,
) {
    val interactionSource = rememberPressInteractionSource()
    TodayQuickCard(
        modifier = Modifier
            .testTag("today_task_${item.id}")
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        title = item.title,
        subtitle = item.remainingTimeText,
        subtitleColor = MaterialTheme.colorScheme.error,
        accentColor = NoteFlowTaskAccent,
        layoutSpec = layoutSpec,
        action = {
            TodayQuickActionButton(
                text = item.actionLabel,
                accentColor = NoteFlowTaskAccent,
                height = layoutSpec.controlHeight,
                width = layoutSpec.actionWidth,
                textStyle = layoutSpec.actionStyle,
                enabled = item.actionEnabled && item.actionType != TaskQuickActionType.NONE,
                testTag = "today_task_action_${item.id}",
                onClick = onAction,
            )
        },
    )
}

@Composable
internal fun TodayHabitCard(
    item: TodayHabitCardUiModel,
    layoutSpec: TodayCompactLayoutSpec,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
) {
    val interactionSource = rememberPressInteractionSource()
    TodayQuickCard(
        modifier = Modifier
            .testTag("today_habit_${item.id}")
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        title = item.title,
        subtitle = item.statusHint,
        support = item.progressText,
        accentColor = NoteFlowHabitAccent,
        layoutSpec = layoutSpec,
        action = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TodayQuickActionButton(
                    text = item.primaryActionLabel,
                    accentColor = NoteFlowHabitAccent,
                    height = layoutSpec.controlHeight,
                    width = layoutSpec.actionWidth,
                    textStyle = layoutSpec.actionStyle,
                    enabled = item.primaryActionEnabled && item.primaryActionType != HabitQuickActionType.NONE,
                    testTag = "today_habit_primary_action_${item.id}",
                    onClick = onPrimaryAction,
                )
                if (!item.secondaryActionLabel.isNullOrBlank()) {
                    TodayQuickActionButton(
                        text = item.secondaryActionLabel,
                        accentColor = NoteFlowHabitAccentSoft,
                        height = layoutSpec.controlHeight,
                        width = layoutSpec.actionWidth,
                        textStyle = layoutSpec.actionStyle,
                        enabled = item.secondaryActionEnabled && item.secondaryActionType != null,
                        testTag = "today_habit_secondary_action_${item.id}",
                        onClick = onSecondaryAction,
                    )
                }
            }
        },
    )
}

@Composable
private fun TodayQuickCard(
    modifier: Modifier,
    title: String,
    subtitle: String? = null,
    subtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    support: String? = null,
    accentColor: Color,
    layoutSpec: TodayCompactLayoutSpec,
    action: @Composable RowScope.() -> Unit,
) {
    val interactionSource = rememberPressInteractionSource()
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .noteFlowPressScale(interactionSource = interactionSource),
        accentColor = accentColor,
        level = GlassLevel.Normal,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(layoutSpec.cardHeight)
                .padding(horizontal = layoutSpec.cardPadding, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    style = layoutSpec.cardTitleStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = layoutSpec.supportStyle,
                        color = subtitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (!support.isNullOrBlank()) {
                    Text(
                        text = support,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            action()
        }
    }
}

@Composable
private fun TodayQuickActionButton(
    text: String,
    accentColor: Color,
    height: Dp,
    width: Dp,
    textStyle: TextStyle,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier
            .height(height)
            .testTag(testTag),
        enabled = enabled,
        onClick = onClick,
        shape = RoundedCornerShape(height / 2),
        colors = noteFlowButtonColors(accentColor),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.widthIn(min = width - 20.dp),
            style = textStyle,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun TodayEmptySectionCard(
    title: String,
    description: String,
    actionLabel: String,
    actionTestTag: String,
    accentColor: Color,
    layoutSpec: TodayCompactLayoutSpec,
    onActionClick: () -> Unit,
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = layoutSpec.emptyCardMinHeight)
            .testTag("${actionTestTag}_card"),
        accentColor = accentColor,
        level = GlassLevel.Normal,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(layoutSpec.cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = title,
                style = layoutSpec.cardTitleStyle,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            Text(
                text = description,
                style = layoutSpec.supportStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(layoutSpec.controlHeight + 4.dp)
                    .testTag(actionTestTag),
                onClick = onActionClick,
                colors = noteFlowOutlinedButtonColors(),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            ) {
                Text(
                    text = actionLabel,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun TodayGlobalEmptyCard(
    onCreateTask: () -> Unit,
    onCreateHabit: () -> Unit,
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("today_empty_state"),
        accentColor = NoteFlowTodayAccent,
        level = GlassLevel.Normal,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "今天还没有需要处理的内容",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "先创建任务或习惯。",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("today_empty_create_task"),
                    onClick = onCreateTask,
                    colors = noteFlowOutlinedButtonColors(),
                ) {
                    Text("新建任务", maxLines = 1)
                }
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("today_empty_create_habit"),
                    onClick = onCreateHabit,
                    colors = noteFlowOutlinedButtonColors(),
                ) {
                    Text("新建习惯", maxLines = 1)
                }
            }
        }
    }
}

@Composable
fun TodayQuickCreateFab(
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onCreateTask: () -> Unit,
    onCreateHabit: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StaggeredQuickCreateAction(
            visible = expanded,
            delayMillis = 0,
            testTag = "today_quick_create_task",
            text = "新建任务",
            accentColor = NoteFlowTaskAccentSoft,
            onClick = onCreateTask,
        )
        StaggeredQuickCreateAction(
            visible = expanded,
            delayMillis = 45,
            testTag = "today_quick_create_habit",
            text = "新建习惯",
            accentColor = NoteFlowHabitAccentSoft,
            onClick = onCreateHabit,
        )

        ModuleFab(
            accentColor = NoteFlowTodayAccent,
            contentDescription = if (expanded) "收起快速新建" else "展开快速新建",
            icon = if (expanded) Icons.Default.Check else Icons.Default.Add,
            testTag = "today_quick_create_main",
            enableRadialExpansion = false,
            onClick = onToggleExpanded,
        )
    }
}

@Composable
private fun StaggeredQuickCreateAction(
    visible: Boolean,
    delayMillis: Int,
    testTag: String,
    text: String,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val interactionSource = rememberPressInteractionSource()
    val radialExpansionController = LocalRadialExpansionController.current
    val actionCenter = remember { mutableStateOf<Offset?>(null) }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 220,
                delayMillis = delayMillis,
                easing = MotionTokens.EasingEmphasized,
            ),
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = delayMillis,
                easing = MotionTokens.EasingEmphasized,
            ),
            initialOffsetY = { it / 2 },
        ) + scaleIn(
            animationSpec = tween(
                durationMillis = 280,
                delayMillis = delayMillis,
                easing = MotionTokens.EasingEmphasized,
            ),
            initialScale = 0.92f,
        ),
        exit = fadeOut(animationSpec = tween(120)) + slideOutVertically(
            animationSpec = tween(160),
            targetOffsetY = { it / 3 },
        ) + scaleOut(
            animationSpec = tween(140),
            targetScale = 0.92f,
        ),
    ) {
        TodayQuickCreateActionSurface(
            testTag = testTag,
            interactionSource = interactionSource,
            accentColor = accentColor,
            onClick = {
                radialExpansionController?.launch(
                    color = accentColor,
                    origin = actionCenter.value,
                    onExpanded = onClick,
                ) ?: onClick()
            },
            onPositioned = { center -> actionCenter.value = center },
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun TodayQuickCreateActionSurface(
    testTag: String,
    interactionSource: MutableInteractionSource,
    accentColor: Color,
    onClick: () -> Unit,
    onPositioned: (Offset) -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    GlassSurface(
        modifier = Modifier
            .testTag(testTag)
            .noteFlowPressScale(interactionSource = interactionSource)
            .onGloballyPositioned { coordinates ->
                onPositioned(coordinates.boundsInRoot().center)
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        accentColor = accentColor,
        level = GlassLevel.Normal,
        shape = RoundedCornerShape(22.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            content = content,
        )
    }
}
