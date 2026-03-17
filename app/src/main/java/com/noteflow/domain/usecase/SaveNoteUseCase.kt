package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.Note
import com.luuzr.jielv.domain.repository.NoteRepository
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    suspend operator fun invoke(note: Note) = repository.saveNote(note)
}
