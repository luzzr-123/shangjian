package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.Task
import com.luuzr.jielv.domain.repository.TaskRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTasksUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    operator fun invoke(includeCompleted: Boolean): Flow<List<Task>> {
        return repository.observeTasks(includeCompleted = includeCompleted)
    }
}
