package com.luuzr.jielv.di

import com.luuzr.jielv.data.local.media.LocalNoteImageStorage
import com.luuzr.jielv.data.local.media.NoteImageStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NoteMediaModule {

    @Binds
    @Singleton
    abstract fun bindNoteImageStorage(
        storage: LocalNoteImageStorage,
    ): NoteImageStorage
}
