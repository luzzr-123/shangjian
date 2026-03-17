package com.luuzr.jielv.domain.repository

import com.luuzr.jielv.data.settings.ReminderPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeReminderPreferences(): Flow<ReminderPreferences>

    suspend fun getReminderPreferences(): ReminderPreferences

    suspend fun updateReminderPreferences(
        transform: (ReminderPreferences) -> ReminderPreferences,
    )

    suspend fun replaceReminderPreferences(preferences: ReminderPreferences)
}
