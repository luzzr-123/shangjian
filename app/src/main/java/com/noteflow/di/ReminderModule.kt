package com.luuzr.jielv.di

import android.content.Context
import androidx.work.WorkManager
import com.luuzr.jielv.core.reminder.ReminderDispatchQueue
import com.luuzr.jielv.core.reminder.ReminderDispatchQueueImpl
import com.luuzr.jielv.core.reminder.ReminderScheduler
import com.luuzr.jielv.core.reminder.ReminderSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
abstract class ReminderModule {

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(
        scheduler: ReminderSchedulerImpl,
    ): ReminderScheduler

    @Binds
    @Singleton
    abstract fun bindReminderDispatchQueue(
        queue: ReminderDispatchQueueImpl,
    ): ReminderDispatchQueue

    companion object {
        @Provides
        @Singleton
        fun provideWorkManager(
            @ApplicationContext context: Context,
        ): WorkManager = WorkManager.getInstance(context)

        @Provides
        @Singleton
        @ApplicationScope
        fun provideApplicationScope(): CoroutineScope {
            return CoroutineScope(SupervisorJob() + Dispatchers.IO)
        }
    }
}
