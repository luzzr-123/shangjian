package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.data.settings.ReminderPreferences
import com.luuzr.jielv.domain.repository.SettingsRepository
import javax.inject.Inject

class GetReminderPreferencesUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(): ReminderPreferences = settingsRepository.getReminderPreferences()
}
