package com.luuzr.jielv.di

import com.luuzr.jielv.data.settings.SettingsRepositoryImpl
import com.luuzr.jielv.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        repository: SettingsRepositoryImpl,
    ): SettingsRepository
}
