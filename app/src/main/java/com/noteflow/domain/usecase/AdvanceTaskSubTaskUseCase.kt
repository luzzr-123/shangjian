package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.TaskSubTaskAdvanceResult
import com.luuzr.jielv.domain.repository.TaskRepository
import javax.inject.Inject

class AdvanceTaskSubTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(taskId: String): TaskSubTaskAdvanceResult {
        return repository.advanceTaskSubTask(taskId)
    }
}
