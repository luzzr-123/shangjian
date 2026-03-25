package com.luuzr.jielv.domain.repository

import com.luuzr.jielv.domain.model.InsertedNoteImage
import com.luuzr.jielv.domain.model.Note
import com.luuzr.jielv.domain.model.NoteDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>

    fun observeDeletedNotes(): Flow<List<Note>> = emptyFlow()

    suspend fun getNote(noteId: String): NoteDetail?

    suspend fun saveNote(note: Note)

    suspend fun softDeleteNote(noteId: String)

    suspend fun restoreNote(noteId: String) = Unit

    suspend fun hardDeleteNote(noteId: String) = Unit

    suspend fun importImage(
        noteId: String,
        sourceUri: String,
    ): InsertedNoteImage

    suspend fun cleanupOrphanedMedia() = Unit

    suspend fun discardDraft(noteId: String)
}
