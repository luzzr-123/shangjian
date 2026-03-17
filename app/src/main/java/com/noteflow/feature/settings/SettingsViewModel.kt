package com.luuzr.jielv.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luuzr.jielv.core.reminder.NotificationPermissionChecker
import com.luuzr.jielv.core.time.TimeProvider
import com.luuzr.jielv.data.settings.ReminderPreferences
import com.luuzr.jielv.domain.usecase.ObserveReminderPreferencesUseCase
import com.luuzr.jielv.domain.usecase.UpdateReminderPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeReminderPreferencesUseCase: ObserveReminderPreferencesUseCase,
    private val updateReminderPreferencesUseCase: UpdateReminderPreferencesUseCase,
    private val notificationPermissionChecker: NotificationPermissionChecker,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeReminderPreferencesUseCase()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSaving = false,
                            errorMessage = throwable.message ?: "设置加载失败，请重试。",
                        )
                    }
                }
                .collect { preferences ->
                    _uiState.update { current ->
                        current.copy(
                            defaultTaskRepeatIntervalText = preferences.defaultTaskRepeatIntervalMinutes.toString(),
                            defaultHabitRepeatIntervalText = preferences.defaultHabitRepeatIntervalMinutes.toString(),
                            notificationPermissionGranted = notificationPermissionChecker.canPostNotifications(),
                            isLoading = false,
                            isSaving = false,
                        )
                    }
                }
        }
    }

    fun onTaskDefaultIntervalChanged(value: String) {
        _uiState.update {
            it.copy(
                defaultTaskRepeatIntervalText = value.filter(Char::isDigit),
                defaultsError = null,
                errorMessage = null,
                resultMessage = null,
            )
        }
    }

    fun onHabitDefaultIntervalChanged(value: String) {
        _uiState.update {
            it.copy(
                defaultHabitRepeatIntervalText = value.filter(Char::isDigit),
                defaultsError = null,
                errorMessage = null,
                resultMessage = null,
            )
        }
    }

    fun saveDefaultIntervals() {
        val taskMinutes = uiState.value.defaultTaskRepeatIntervalText.toIntOrNull()
        val habitMinutes = uiState.value.defaultHabitRepeatIntervalText.toIntOrNull()
        if (taskMinutes == null || taskMinutes <= 0 || habitMinutes == null || habitMinutes <= 0) {
            _uiState.update {
                it.copy(
                    defaultsError = "默认提醒间隔必须是大于 0 的整数分钟。",
                    errorMessage = null,
                    resultMessage = null,
                )
            }
            return
        }
        updatePreferences(successMessage = "提醒偏好已保存。") {
            it.copy(
                defaultTaskRepeatIntervalMinutes = taskMinutes,
                defaultHabitRepeatIntervalMinutes = habitMinutes,
                settingsUpdatedAt = timeProvider.nowMillis(),
            )
        }
    }

    private fun updatePreferences(
        successMessage: String,
        transform: (ReminderPreferences) -> ReminderPreferences,
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null,
                    resultMessage = null,
                )
            }
            runCatching {
                withContext(Dispatchers.IO) {
                    updateReminderPreferencesUseCase(transform)
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        defaultsError = null,
                        errorMessage = null,
                        resultMessage = successMessage,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "设置保存失败，请重试。",
                    )
                }
            }
        }
    }
}
