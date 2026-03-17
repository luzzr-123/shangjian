package com.luuzr.jielv.feature.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luuzr.jielv.core.designsystem.theme.NoteFlowTodayAccent
import com.luuzr.jielv.core.ui.GlassLevel
import com.luuzr.jielv.core.ui.GlassSurface
import com.luuzr.jielv.core.ui.LayoutTokens
import com.luuzr.jielv.core.ui.NoteFlowPageHeader
import com.luuzr.jielv.core.ui.StandardFieldRow
import com.luuzr.jielv.core.ui.StandardSectionCard
import com.luuzr.jielv.core.ui.noteFlowButtonColors
import com.luuzr.jielv.core.ui.noteFlowOutlinedButtonColors
import com.luuzr.jielv.core.ui.noteFlowOutlinedTextFieldColors

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    onOpenTrash: () -> Unit,
    onOpenBackup: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    SettingsScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onTaskDefaultIntervalChanged = viewModel::onTaskDefaultIntervalChanged,
        onHabitDefaultIntervalChanged = viewModel::onHabitDefaultIntervalChanged,
        onSaveDefaults = viewModel::saveDefaultIntervals,
        onOpenNotificationSettings = {
            context.startActivity(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                },
            )
        },
        onOpenTrash = onOpenTrash,
        onOpenBackup = onOpenBackup,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onNavigateBack: () -> Unit,
    onTaskDefaultIntervalChanged: (String) -> Unit,
    onHabitDefaultIntervalChanged: (String) -> Unit,
    onSaveDefaults: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenTrash: () -> Unit,
    onOpenBackup: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("settings_content")
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = LayoutTokens.ScreenHorizontalPadding,
                    vertical = LayoutTokens.ScreenVerticalPadding,
                ),
            verticalArrangement = Arrangement.spacedBy(LayoutTokens.Space16),
        ) {
            TextButton(
                modifier = Modifier.testTag("settings_back"),
                onClick = onNavigateBack,
                enabled = !uiState.isSaving,
            ) {
                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                Text("返回", modifier = Modifier.padding(start = LayoutTokens.Space8))
            }
            NoteFlowPageHeader(
                title = uiState.title,
                subtitle = "管理提醒偏好和数据工具。",
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.testTag("settings_loading"))
            }

            StandardSectionCard(
                title = "提醒偏好",
                accentColor = NoteFlowTodayAccent,
            ) {
                StandardFieldRow(label = "任务默认重复提醒间隔（分钟）") {
                    OutlinedTextField(
                        value = uiState.defaultTaskRepeatIntervalText,
                        onValueChange = onTaskDefaultIntervalChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_task_default_interval"),
                        singleLine = true,
                        enabled = !uiState.isSaving,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = noteFlowOutlinedTextFieldColors(),
                    )
                }
                StandardFieldRow(label = "习惯默认重复提醒间隔（分钟）") {
                    OutlinedTextField(
                        value = uiState.defaultHabitRepeatIntervalText,
                        onValueChange = onHabitDefaultIntervalChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_habit_default_interval"),
                        singleLine = true,
                        enabled = !uiState.isSaving,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = noteFlowOutlinedTextFieldColors(),
                    )
                }
                StandardFieldRow(
                    label = "通知权限",
                    description = if (uiState.notificationPermissionGranted) {
                        "当前已授予通知权限"
                    } else {
                        "当前未授予通知权限"
                    },
                ) {
                    OutlinedButton(
                        onClick = onOpenNotificationSettings,
                        enabled = !uiState.isSaving,
                        colors = noteFlowOutlinedButtonColors(),
                    ) {
                        Text("系统设置")
                    }
                }
                uiState.defaultsError?.let {
                    Text(
                        text = it,
                        modifier = Modifier.testTag("settings_defaults_error"),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_save_defaults"),
                    enabled = !uiState.isSaving,
                    onClick = onSaveDefaults,
                    colors = noteFlowButtonColors(NoteFlowTodayAccent),
                ) {
                    Text(if (uiState.isSaving) "保存中" else "保存提醒偏好")
                }
            }

            StandardSectionCard(
                title = "数据管理",
                accentColor = NoteFlowTodayAccent,
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_open_trash"),
                    onClick = onOpenTrash,
                    enabled = !uiState.isSaving,
                    colors = noteFlowOutlinedButtonColors(),
                ) {
                    Text("打开回收站")
                }
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_open_backup"),
                    onClick = onOpenBackup,
                    enabled = !uiState.isSaving,
                    colors = noteFlowOutlinedButtonColors(),
                ) {
                    Text("备份与恢复")
                }
            }

            uiState.errorMessage?.let {
                Text(
                    text = it,
                    modifier = Modifier.testTag("settings_error"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            uiState.resultMessage?.let {
                Text(
                    text = it,
                    modifier = Modifier.testTag("settings_result"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun TopLevelSettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassSurface(
        modifier = modifier.testTag("open_settings"),
        shape = RoundedCornerShape(20.dp),
        accentColor = NoteFlowTodayAccent,
        level = GlassLevel.Weak,
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "设置",
                tint = NoteFlowTodayAccent.copy(alpha = 0.94f),
            )
        }
    }
}
