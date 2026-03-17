package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.repository.TaskRepository
import javax.inject.Inject

class ToggleSubTaskCompletedUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(taskId: String, subTaskId: String, completed: Boolean) {
        repository.setSubTaskCompleted(
            taskId = taskId,
            subTaskId = subTaskId,
            completed = completed,
        )
    }
}
