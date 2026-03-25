package com.luuzr.jielv

import android.app.Application
import com.luuzr.jielv.core.reminder.ReminderNotificationManager
import com.luuzr.jielv.core.reminder.ReminderRecoveryCoordinator
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.luuzr.jielv.di.ApplicationScope
import com.luuzr.jielv.domain.repository.NoteRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class NoteFlowApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var reminderRecoveryCoordinator: ReminderRecoveryCoordinator

    @Inject
    lateinit var reminderNotificationManager: ReminderNotificationManager

    @Inject
    lateinit var noteRepository: NoteRepository

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        reminderNotificationManager.ensureChannel()
        reminderRecoveryCoordinator.ensureHealthCheckScheduled()
        reminderRecoveryCoordinator.enqueueImmediateRecovery()
        applicationScope.launch {
            runCatching { noteRepository.cleanupOrphanedMedia() }
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
