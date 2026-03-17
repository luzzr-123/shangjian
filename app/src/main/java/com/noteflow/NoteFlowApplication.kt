package com.luuzr.jielv

import android.app.Application
import com.luuzr.jielv.core.reminder.ReminderNotificationManager
import com.luuzr.jielv.core.reminder.ReminderRecoveryCoordinator
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NoteFlowApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var reminderRecoveryCoordinator: ReminderRecoveryCoordinator

    @Inject
    lateinit var reminderNotificationManager: ReminderNotificationManager

    override fun onCreate() {
        super.onCreate()
        reminderNotificationManager.ensureChannel()
        reminderRecoveryCoordinator.ensureHealthCheckScheduled()
        reminderRecoveryCoordinator.enqueueImmediateRecovery()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
