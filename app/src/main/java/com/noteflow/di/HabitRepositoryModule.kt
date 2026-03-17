package com.luuzr.jielv.di

import com.luuzr.jielv.data.repository.HabitRepositoryImpl
import com.luuzr.jielv.domain.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HabitRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        repository: HabitRepositoryImpl,
    ): HabitRepository
}
