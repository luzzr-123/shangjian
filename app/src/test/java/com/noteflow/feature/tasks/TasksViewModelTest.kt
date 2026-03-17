package com.luuzr.jielv.feature.tasks

import com.luuzr.jielv.MainDispatcherRule
import com.luuzr.jielv.core.time.TimeProvider
import com.luuzr.jielv.domain.model.SubTask
import com.luuzr.jielv.domain.model.Task
import com.luuzr.jielv.domain.model.TaskCompletionRule
import com.luuzr.jielv.domain.model.TaskPriority
import com.luuzr.jielv.domain.model.TaskStatus
import com.luuzr.jielv.domain.repository.TaskRepository
import com.luuzr.jielv.domain.usecase.ObserveTasksUseCase
import com.luuzr.jielv.domain.usecase.ToggleTaskCompletedUseCase
import java.time.ZoneId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TasksViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun hidesCompletedTasksByDefaultAndShowsThemWhenEnabled() = runTest {
        val repository = FakeTaskRepository()
        repository.tasks.value = listOf(
            task(id = "1", title = "未完成任务", status = TaskStatus.ACTIVE),
            task(id = "2", title = "已完成任务", status = TaskStatus.COMPLETED),
        )

        val viewModel = TasksViewModel(
            observeTasksUseCase = ObserveTasksUseCase(repository),
            toggleTaskCompletedUseCase = ToggleTaskCompletedUseCase(repository),
            timeProvider = FixedTimeProvider(),
        )
        val job = backgroundScope.launch {
            viewModel.uiState.collect { }
        }

        advanceUntilIdle()
        assertEquals(listOf("未完成任务"), viewModel.uiState.value.tasks.map { it.title })

        viewModel.onShowCompletedChanged(true)
        advanceUntilIdle()

        assertEquals(
            listOf("未完成任务", "已完成任务"),
            viewModel.uiState.value.tasks.map { it.title },
        )

        job.cancel()
    }

    private class FixedTimeProvider : TimeProvider {
        override fun nowMillis(): Long = 1_730_000_000_000L

        override fun zoneId(): ZoneId = ZoneId.of("Asia/Singapore")
    }

    private class FakeTaskRepository : TaskRepository {
        val tasks = MutableStateFlow<List<Task>>(emptyList())

        override fun observeTasks(includeCompleted: Boolean): Flow<List<Task>> {
            return tasks.map { value ->
                if (includeCompleted) {
                    value
                } else {
                    value.filterNot { it.status == TaskStatus.COMPLETED }
                }
            }
        }

        override suspend fun getTask(taskId: String): Task? = tasks.value.firstOrNull { it.id == taskId }

        override suspend fun saveTask(task: Task, subTasks: List<SubTask>) = Unit

        override suspend fun softDeleteTask(taskId: String) = Unit

        override suspend fun setTaskCompleted(taskId: String, completed: Boolean) = Unit

        override suspend fun setSubTaskCompleted(taskId: String, subTaskId: String, completed: Boolean) = Unit
    }

    private fun task(
        id: String,
        title: String,
        status: TaskStatus,
    ): Task {
        return Task(
            id = id,
            title = title,
            priority = TaskPriority.NORMAL,
            status = status,
            completionRule = TaskCompletionRule.MANUAL,
        )
    }
}
