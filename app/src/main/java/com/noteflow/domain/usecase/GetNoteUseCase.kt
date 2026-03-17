package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.NoteDetail
import com.luuzr.jielv.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    suspend operator fun invoke(noteId: String): NoteDetail? = repository.getNote(noteId)
}
