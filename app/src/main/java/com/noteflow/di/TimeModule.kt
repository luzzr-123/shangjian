package com.luuzr.jielv.di

import com.luuzr.jielv.core.time.SystemTimeProvider
import com.luuzr.jielv.core.time.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeModule {

    @Binds
    @Singleton
    abstract fun bindTimeProvider(
        provider: SystemTimeProvider,
    ): TimeProvider
}
