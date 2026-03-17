package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.InsertedNoteImage
import com.luuzr.jielv.domain.repository.NoteRepository
import javax.inject.Inject

class ImportNoteImageUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    suspend operator fun invoke(
        noteId: String,
        sourceUri: String,
    ): InsertedNoteImage {
        return repository.importImage(noteId, sourceUri)
    }
}
