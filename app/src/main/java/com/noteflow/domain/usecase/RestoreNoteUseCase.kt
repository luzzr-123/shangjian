package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.repository.NoteRepository
import javax.inject.Inject

class RestoreNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
) {
    suspend operator fun invoke(noteId: String) {
        noteRepository.restoreNote(noteId)
    }
}
