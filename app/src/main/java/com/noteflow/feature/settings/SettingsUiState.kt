package com.luuzr.jielv.feature.settings

data class SettingsUiState(
    val title: String = "设置",
    val defaultTaskRepeatIntervalText: String = "",
    val defaultHabitRepeatIntervalText: String = "",
    val notificationPermissionGranted: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val defaultsError: String? = null,
    val errorMessage: String? = null,
    val resultMessage: String? = null,
)
