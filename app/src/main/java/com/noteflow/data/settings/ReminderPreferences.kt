package com.luuzr.jielv.data.settings

data class ReminderPreferences(
    val defaultTaskRepeatIntervalMinutes: Int = 60,
    val defaultHabitRepeatIntervalMinutes: Int = 60,
    val settingsUpdatedAt: Long = 0L,
)
