package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.SubTask
import com.luuzr.jielv.domain.model.Task
import com.luuzr.jielv.domain.repository.TaskRepository
import javax.inject.Inject

class SaveTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(task: Task, subTasks: List<SubTask>) {
        repository.saveTask(task = task, subTasks = subTasks)
    }
}
