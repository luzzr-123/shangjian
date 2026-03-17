package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.data.settings.ReminderPreferences
import com.luuzr.jielv.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveReminderPreferencesUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<ReminderPreferences> = settingsRepository.observeReminderPreferences()
}
