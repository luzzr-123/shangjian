package com.luuzr.jielv.di

import com.luuzr.jielv.data.backup.BackupRepositoryImpl
import com.luuzr.jielv.domain.repository.BackupRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BackupModule {

    @Binds
    @Singleton
    abstract fun bindBackupRepository(
        repository: BackupRepositoryImpl,
    ): BackupRepository
}
