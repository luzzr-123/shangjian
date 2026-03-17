package com.luuzr.jielv.data.local.media

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class StoredNoteImage(
    val mediaId: String,
    val localPath: String,
    val mimeType: String,
    val sizeBytes: Long,
)

interface NoteImageStorage {
    fun importImage(
        noteId: String,
        sourceUri: String,
    ): StoredNoteImage

    fun deleteImage(localPath: String)
}

@Singleton
class LocalNoteImageStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) : NoteImageStorage {

    override fun importImage(
        noteId: String,
        sourceUri: String,
    ): StoredNoteImage {
        val uri = Uri.parse(sourceUri)
        val mimeType = context.contentResolver.getType(uri).orEmpty().ifBlank { "image/*" }
        val mediaId = UUID.randomUUID().toString()
        val extension = resolveExtension(
            contentResolver = context.contentResolver,
            uri = uri,
            mimeType = mimeType,
        )
        val noteDirectory = File(context.filesDir, "media/notes/$noteId").apply { mkdirs() }
        val targetFile = File(noteDirectory, "$mediaId.$extension")

        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Unable to open image uri: $sourceUri" }
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return StoredNoteImage(
            mediaId = mediaId,
            localPath = targetFile.absolutePath,
            mimeType = mimeType,
            sizeBytes = targetFile.length(),
        )
    }

    override fun deleteImage(localPath: String) {
        runCatching {
            File(localPath).takeIf { it.exists() }?.delete()
        }
    }

    private fun resolveExtension(
        contentResolver: ContentResolver,
        uri: Uri,
        mimeType: String,
    ): String {
        val displayName = queryDisplayName(contentResolver, uri)
        val fromName = displayName?.substringAfterLast('.', missingDelimiterValue = "").orEmpty()
        if (fromName.isNotBlank()) return fromName
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType).orEmpty().ifBlank { "jpg" }
    }

    private fun queryDisplayName(
        contentResolver: ContentResolver,
        uri: Uri,
    ): String? {
        return contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(0)
                } else {
                    null
                }
            }
    }
}
