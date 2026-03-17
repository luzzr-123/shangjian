package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.repository.TaskRepository
import javax.inject.Inject

class RestoreTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(taskId: String) {
        taskRepository.restoreTask(taskId)
    }
}
