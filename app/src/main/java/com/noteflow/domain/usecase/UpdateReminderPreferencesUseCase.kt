package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.core.reminder.ReminderDispatchQueue
import com.luuzr.jielv.data.settings.ReminderPreferences
import com.luuzr.jielv.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateReminderPreferencesUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val reminderDispatchQueue: ReminderDispatchQueue,
) {
    suspend operator fun invoke(
        transform: (ReminderPreferences) -> ReminderPreferences,
    ) {
        settingsRepository.updateReminderPreferences(transform)
        reminderDispatchQueue.rescheduleAllActiveReminders()
    }
}
