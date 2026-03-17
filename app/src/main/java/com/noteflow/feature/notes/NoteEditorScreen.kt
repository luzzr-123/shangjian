package com.luuzr.jielv.feature.notes

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luuzr.jielv.core.designsystem.theme.NoteFlowNoteAccent
import com.luuzr.jielv.core.markdown.MarkdownRenderer
import com.luuzr.jielv.core.ui.NoteFlowEmptyStateCard
import com.luuzr.jielv.core.ui.NoteFlowPageHeader
import com.luuzr.jielv.core.ui.NoteFlowSectionCard
import com.luuzr.jielv.core.ui.noteFlowButtonColors
import com.luuzr.jielv.core.ui.noteFlowOutlinedButtonColors
import com.luuzr.jielv.core.ui.noteFlowOutlinedTextFieldColors

@Composable
fun NoteEditorRoute(
    onNavigateBack: () -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let { viewModel.onInsertImage(it.toString()) }
    }

    BackHandler(enabled = !uiState.isSaving) {
        if (viewModel.onDiscardClicked()) {
            onNavigateBack()
        }
    }

    NoteEditorScreen(
        uiState = uiState,
        onNavigateBack = {
            if (!uiState.isSaving && viewModel.onDiscardClicked()) {
                onNavigateBack()
            }
        },
        onTitleChanged = viewModel::onTitleChanged,
        onContentChanged = viewModel::onContentChanged,
        onPickImage = {
            imagePickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
        onSaveClicked = {
            if (viewModel.validateBeforeSave()) {
                viewModel.saveNote(onSaved = onNavigateBack)
            }
        },
        onDeleteClicked = {
            viewModel.onDeleteClicked(onDeleted = onNavigateBack)
        },
    )
}

@Composable
fun NoteEditorScreen(
    uiState: NoteEditorUiState,
    onNavigateBack: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (TextFieldValue) -> Unit,
    onPickImage: () -> Unit,
    onSaveClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { innerPadding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(horizontal = 24.dp))
            }
        } else if (uiState.hasMissingContent) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TextButton(onClick = onNavigateBack) {
                    Text("返回")
                }
                NoteFlowEmptyStateCard(
                    title = "笔记不存在",
                    description = uiState.loadErrorMessage ?: "这条笔记可能已经被删除。",
                    accentColor = NoteFlowNoteAccent,
                    actionLabel = "返回列表",
                    actionTestTag = "note_editor_go_back",
                    onActionClick = onNavigateBack,
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TextButton(onClick = onNavigateBack, enabled = !uiState.isSaving) {
                    Text("返回")
                }
                NoteFlowPageHeader(
                    title = uiState.screenTitle,
                    subtitle = "保持简洁输入，实时预览 Markdown。",
                )

                NoteFlowSectionCard(title = "基本信息", subtitle = null) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_editor_title_input"),
                        value = uiState.title,
                        onValueChange = onTitleChanged,
                        label = { Text("标题") },
                        singleLine = true,
                        enabled = !uiState.isSaving,
                        isError = uiState.titleError != null,
                        supportingText = { uiState.titleError?.let { Text(it) } },
                        colors = noteFlowOutlinedTextFieldColors(),
                    )

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_editor_insert_image"),
                        onClick = onPickImage,
                        enabled = !uiState.isSaving,
                        colors = noteFlowButtonColors(NoteFlowNoteAccent),
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null)
                        Text(text = if (uiState.isSaving) "处理中" else "插入图片", modifier = Modifier.padding(start = 8.dp))
                    }

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_editor_content_input"),
                        value = uiState.content,
                        onValueChange = onContentChanged,
                        enabled = !uiState.isSaving,
                        label = { Text("正文（Markdown 原文）") },
                        minLines = 12,
                        visualTransformation = NoteImageReferenceVisualTransformation,
                        colors = noteFlowOutlinedTextFieldColors(),
                    )
                }

                NoteFlowSectionCard(
                    title = "实时预览",
                    subtitle = null,
                    modifier = Modifier.testTag("note_markdown_preview"),
                ) {
                    if (uiState.content.text.isBlank()) {
                        Text(
                            text = "输入 Markdown 后会在这里预览。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        MarkdownRenderer(
                            markdown = uiState.content.text,
                            mediaLookup = uiState.images.associate { it.mediaId to it.localPath },
                        )
                    }
                }

                NoteFlowSectionCard(title = "操作", subtitle = null) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onNavigateBack,
                        enabled = !uiState.isSaving,
                        colors = noteFlowOutlinedButtonColors(),
                    ) {
                        Text("放弃返回")
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_editor_save"),
                        onClick = onSaveClicked,
                        enabled = !uiState.isSaving,
                        colors = noteFlowButtonColors(NoteFlowNoteAccent),
                    ) {
                        Text(if (uiState.isSaving) "保存中" else uiState.saveButtonLabel)
                    }
                    if (uiState.canDelete) {
                        HorizontalDivider()
                        TextButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("note_editor_delete"),
                            onClick = onDeleteClicked,
                            enabled = !uiState.isSaving,
                        ) {
                            Text(
                                text = "软删除笔记",
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                    uiState.saveErrorMessage?.let {
                        Text(
                            text = it,
                            modifier = Modifier.testTag("note_editor_save_error"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}
