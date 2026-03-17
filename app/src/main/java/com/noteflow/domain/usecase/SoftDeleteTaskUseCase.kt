package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.repository.TaskRepository
import javax.inject.Inject

class SoftDeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(taskId: String) {
        repository.softDeleteTask(taskId)
    }
}
