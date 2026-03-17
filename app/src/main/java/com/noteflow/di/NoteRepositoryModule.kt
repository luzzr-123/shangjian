package com.luuzr.jielv.di

import com.luuzr.jielv.data.repository.NoteRepositoryImpl
import com.luuzr.jielv.domain.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NoteRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        repository: NoteRepositoryImpl,
    ): NoteRepository
}
