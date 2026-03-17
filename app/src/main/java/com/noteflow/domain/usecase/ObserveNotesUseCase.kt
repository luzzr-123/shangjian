package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.Note
import com.luuzr.jielv.domain.repository.NoteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveNotesUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    operator fun invoke(): Flow<List<Note>> = repository.observeNotes()
}
