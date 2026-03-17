package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.repository.TaskRepository
import javax.inject.Inject

class ToggleTaskCompletedUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(taskId: String, completed: Boolean) {
        repository.setTaskCompleted(taskId = taskId, completed = completed)
    }
}
