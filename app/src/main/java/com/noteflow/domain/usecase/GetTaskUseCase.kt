package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.Task
import com.luuzr.jielv.domain.repository.TaskRepository
import javax.inject.Inject

class GetTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(taskId: String): Task? = repository.getTask(taskId)
}
