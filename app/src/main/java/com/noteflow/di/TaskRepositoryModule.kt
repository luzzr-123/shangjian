package com.luuzr.jielv.di

import com.luuzr.jielv.data.repository.TaskRepositoryImpl
import com.luuzr.jielv.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TaskRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        repository: TaskRepositoryImpl,
    ): TaskRepository
}
