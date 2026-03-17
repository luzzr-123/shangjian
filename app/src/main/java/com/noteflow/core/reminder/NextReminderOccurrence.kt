package com.luuzr.jielv.core.reminder

data class NextReminderOccurrence(
    val atMillis: Long,
    val reason: ReminderTriggerReason,
)
