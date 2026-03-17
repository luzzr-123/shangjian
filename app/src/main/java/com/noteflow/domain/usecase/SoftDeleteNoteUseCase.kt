package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.repository.NoteRepository
import javax.inject.Inject

class SoftDeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    suspend operator fun invoke(noteId: String) = repository.softDeleteNote(noteId)
}
