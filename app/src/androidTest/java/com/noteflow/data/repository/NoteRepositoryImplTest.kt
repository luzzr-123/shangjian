package com.luuzr.jielv.data.repository

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.luuzr.jielv.core.markdown.MarkdownImageReferenceParser
import com.luuzr.jielv.core.markdown.MarkdownPreviewTextExtractor
import com.luuzr.jielv.core.time.TimeProvider
import com.luuzr.jielv.data.local.database.NoteFlowDatabase
import com.luuzr.jielv.data.local.media.NoteImageStorage
import com.luuzr.jielv.data.local.media.StoredNoteImage
import com.luuzr.jielv.domain.model.Note
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteRepositoryImplTest {

    private lateinit var database: NoteFlowDatabase
    private lateinit var repository: NoteRepositoryImpl
    private lateinit var fakeStorage: FakeNoteImageStorage
    private val timeProvider = MutableTimeProvider()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(
            context,
            NoteFlowDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
        fakeStorage = FakeNoteImageStorage()
        repository = NoteRepositoryImpl(
            noteDao = database.noteDao(),
            mediaDao = database.mediaDao(),
            timeProvider = timeProvider,
            noteImageStorage = fakeStorage,
            previewTextExtractor = MarkdownPreviewTextExtractor(),
            imageReferenceParser = MarkdownImageReferenceParser(),
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun sortsNotesByRecentEditTime() = runBlocking {
        timeProvider.now = epochMillis(2026, 3, 9, 9, 0)
        repository.saveNote(note(title = "First"))
        timeProvider.now = epochMillis(2026, 3, 9, 10, 0)
        repository.saveNote(note(title = "Second"))

        val titles = repository.observeNotes().first().map { it.title }

        assertEquals(listOf("Second", "First"), titles)
    }

    @Test
    fun importImageCreatesMediaRowAndMarkdownReference() = runBlocking {
        val noteId = UUID.randomUUID().toString()

        val inserted = repository.importImage(noteId, "content://image")

        assertEquals("![image](local://media/media-1)", inserted.markdownReference)
        assertEquals(
            listOf("media-1"),
            database.mediaDao().getActiveMediaForOwner("note", noteId).map { it.id },
        )
    }

    @Test
    fun saveWithoutImageReferenceSoftDeletesRemovedMedia() = runBlocking {
        val noteId = UUID.randomUUID().toString()
        val inserted = repository.importImage(noteId, "content://image")
        repository.saveNote(
            note(
                id = noteId,
                title = "With image",
                contentMarkdown = inserted.markdownReference,
            ),
        )

        repository.saveNote(
            note(
                id = noteId,
                title = "Without image",
                contentMarkdown = "正文",
            ),
        )

        val media = database.mediaDao().getMediaById("media-1")
        assertTrue(media?.isDeleted == true)
        assertEquals(listOf("/virtual/media-1.jpg"), fakeStorage.deletedPaths)
    }

    @Test
    fun softDeleteHidesNoteAndMedia() = runBlocking {
        val noteId = UUID.randomUUID().toString()
        repository.importImage(noteId, "content://image")
        repository.saveNote(note(id = noteId, title = "Delete me", contentMarkdown = "正文"))

        repository.softDeleteNote(noteId)

        assertTrue(repository.observeNotes().first().isEmpty())
        assertTrue(database.mediaDao().getActiveMediaForOwner("note", noteId).isEmpty())
    }

    @Test
    fun restoreNoteRestoresDeletedMedia() = runBlocking {
        val noteId = UUID.randomUUID().toString()
        val inserted = repository.importImage(noteId, "content://image")
        repository.saveNote(note(id = noteId, title = "Restore me", contentMarkdown = inserted.markdownReference))
        repository.softDeleteNote(noteId)

        repository.restoreNote(noteId)

        val restored = repository.getNote(noteId)
        assertEquals(noteId, restored?.note?.id)
        assertEquals(listOf("media-1"), restored?.images?.map { it.mediaId })
    }

    @Test
    fun hardDeleteRemovesNoteAndDeletesFiles() = runBlocking {
        val noteId = UUID.randomUUID().toString()
        repository.importImage(noteId, "content://image")
        repository.saveNote(note(id = noteId, title = "Hard delete", contentMarkdown = "正文"))

        repository.hardDeleteNote(noteId)

        assertTrue(database.noteDao().getNote(noteId) == null)
        assertTrue(database.mediaDao().getAllMedia().none { it.ownerId == noteId })
        assertEquals(setOf("/virtual/media-1.jpg"), fakeStorage.deletedPaths.toSet())
    }

    private fun note(
        id: String = UUID.randomUUID().toString(),
        title: String,
        contentMarkdown: String? = null,
    ): Note {
        return Note(
            id = id,
            title = title,
            contentMarkdown = contentMarkdown,
        )
    }

    private fun epochMillis(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
    ): Long {
        return LocalDateTime.of(year, month, day, hour, minute)
            .atZone(ZoneId.of("Asia/Singapore"))
            .toInstant()
            .toEpochMilli()
    }

    private class MutableTimeProvider : TimeProvider {
        var now: Long = 0L

        override fun nowMillis(): Long = now

        override fun zoneId(): ZoneId = ZoneId.of("Asia/Singapore")
    }

    private class FakeNoteImageStorage : NoteImageStorage {
        private var nextId = 1
        val deletedPaths = mutableListOf<String>()

        override fun importImage(noteId: String, sourceUri: String): StoredNoteImage {
            val mediaId = "media-${nextId++}"
            return StoredNoteImage(
                mediaId = mediaId,
                localPath = "/virtual/$mediaId.jpg",
                mimeType = "image/jpeg",
                sizeBytes = 42L,
            )
        }

        override fun deleteImage(localPath: String) {
            deletedPaths += localPath
        }
    }
}
