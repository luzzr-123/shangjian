package com.luuzr.jielv.feature.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luuzr.jielv.core.time.TimeProvider
import com.luuzr.jielv.domain.usecase.ObserveNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class NotesViewModel @Inject constructor(
    observeNotesUseCase: ObserveNotesUseCase,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    val uiState: StateFlow<NotesUiState> = observeNotesUseCase()
        .map { notes ->
            NotesUiState(
                notes = notes.map { note ->
                    NoteCardUiModel(
                        id = note.id,
                        title = note.title,
                        previewText = note.previewText.orEmpty().ifBlank { "暂无正文预览" },
                        updatedAtText = formatUpdatedAt(note.updatedAt),
                    )
                },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotesUiState(),
        )

    private fun formatUpdatedAt(updatedAt: Long): String {
        return DateTimeFormatter.ofPattern("MM-dd HH:mm")
            .format(
                Instant.ofEpochMilli(updatedAt)
                    .atZone(timeProvider.zoneId()),
            )
    }
}

