package com.luuzr.jielv.feature.today

import com.luuzr.jielv.MainDispatcherRule
import com.luuzr.jielv.core.time.RemainingTimeFormatter
import com.luuzr.jielv.core.time.TimeProvider
import com.luuzr.jielv.domain.model.Habit
import com.luuzr.jielv.domain.model.HabitCheckInMode
import com.luuzr.jielv.domain.model.HabitStepAdvanceResult
import com.luuzr.jielv.domain.model.InsertedNoteImage
import com.luuzr.jielv.domain.model.Note
import com.luuzr.jielv.domain.model.NoteDetail
import com.luuzr.jielv.domain.model.SubTask
import com.luuzr.jielv.domain.model.Task
import com.luuzr.jielv.domain.model.TaskCompletionRule
import com.luuzr.jielv.domain.model.TaskStatus
import com.luuzr.jielv.domain.model.TaskSubTaskAdvanceResult
import com.luuzr.jielv.domain.repository.HabitRepository
import com.luuzr.jielv.domain.repository.NoteRepository
import com.luuzr.jielv.domain.repository.TaskRepository
import com.luuzr.jielv.domain.usecase.AdvanceHabitStepUseCase
import com.luuzr.jielv.domain.usecase.AdvanceTaskSubTaskUseCase
import com.luuzr.jielv.domain.usecase.CheckInPolicy
import com.luuzr.jielv.domain.usecase.CompleteCheckHabitUseCase
import com.luuzr.jielv.domain.usecase.FinishHabitDurationUseCase
import com.luuzr.jielv.domain.usecase.HabitQuickActionType
import com.luuzr.jielv.domain.usecase.HabitScheduleEvaluator
import com.luuzr.jielv.domain.usecase.ObserveHabitsUseCase
import com.luuzr.jielv.domain.usecase.ObserveNotesUseCase
import com.luuzr.jielv.domain.usecase.ObserveTasksUseCase
import com.luuzr.jielv.domain.usecase.ObserveTodayOverviewUseCase
import com.luuzr.jielv.domain.usecase.PauseHabitDurationUseCase
import com.luuzr.jielv.domain.usecase.RevertHabitStepUseCase
import com.luuzr.jielv.domain.usecase.StartHabitDurationUseCase
import com.luuzr.jielv.domain.usecase.ToggleSubTaskCompletedUseCase
import com.luuzr.jielv.domain.usecase.ToggleTaskCompletedUseCase
import com.luuzr.jielv.domain.usecase.UndoHabitCompletionUseCase
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodayViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun maps_quick_cards_and_counts_in_same_scope() = runTest {
        val timeProvider = FixedTimeProvider(LocalDate.of(2026, 3, 13))
        val taskRepository = FakeTaskRepository(
            tasks = listOf(
                Task(
                    id = "task-1",
                    title = "提交周报",
                    status = TaskStatus.ACTIVE,
                    completionRule = TaskCompletionRule.MANUAL,
                    dueAt = timeProvider.nowMillis() + 2 * 60 * 60 * 1_000L,
                ),
            ),
        )
        val habitRepository = FakeHabitRepository(
            habits = listOf(
                Habit(
                    id = "habit-1",
                    title = "喝水",
                    checkInMode = HabitCheckInMode.CHECK,
                ),
            ),
        )
        val noteRepository = FakeNoteRepository()
        val viewModel = createViewModel(taskRepository, habitRepository, noteRepository, timeProvider)

        primeUiState(viewModel)

        val state = viewModel.uiState.value
        assertEquals(1, state.summary.pendingTaskCount)
        assertEquals(1, state.summary.dueHabitCount)
        assertEquals(1, state.tasks.size)
        assertEquals(1, state.habits.size)
        assertTrue(state.tasks.first().remainingTimeText?.isNotBlank() == true)
    }

    @Test
    fun task_and_habit_quick_actions_dispatch_repository_operations() = runTest {
        val timeProvider = FixedTimeProvider(LocalDate.of(2026, 3, 13))
        val taskRepository = FakeTaskRepository(
            tasks = listOf(
                Task(
                    id = "task-1",
                    title = "提交周报",
                    status = TaskStatus.ACTIVE,
                    completionRule = TaskCompletionRule.MANUAL,
                ),
            ),
        )
        val habitRepository = FakeHabitRepository(
            habits = listOf(
                Habit(
                    id = "habit-1",
                    title = "喝水",
                    checkInMode = HabitCheckInMode.CHECK,
                ),
            ),
        )
        val noteRepository = FakeNoteRepository()
        val viewModel = createViewModel(taskRepository, habitRepository, noteRepository, timeProvider)
        primeUiState(viewModel)

        viewModel.onTaskAction("task-1", com.luuzr.jielv.domain.usecase.TaskQuickActionType.COMPLETE)
        viewModel.onHabitPrimaryAction(
            habitId = "habit-1",
            actionType = HabitQuickActionType.CHECK,
            durationRunning = false,
        )
        advanceUntilIdle()

        assertEquals(listOf("task-1:true"), taskRepository.taskCompleteEvents)
        assertEquals(listOf("habit-1"), habitRepository.checkCompleteEvents)
    }

    private fun createViewModel(
        taskRepository: FakeTaskRepository,
        habitRepository: FakeHabitRepository,
        noteRepository: FakeNoteRepository,
        timeProvider: TimeProvider,
    ): TodayViewModel {
        val checkInPolicy = CheckInPolicy(timeProvider)
        return TodayViewModel(
            observeTodayOverviewUseCase = ObserveTodayOverviewUseCase(
                observeTasksUseCase = ObserveTasksUseCase(taskRepository),
                observeHabitsUseCase = ObserveHabitsUseCase(habitRepository),
                observeNotesUseCase = ObserveNotesUseCase(noteRepository),
                habitScheduleEvaluator = HabitScheduleEvaluator(),
                timeProvider = timeProvider,
            ),
            toggleTaskCompletedUseCase = ToggleTaskCompletedUseCase(taskRepository),
            toggleSubTaskCompletedUseCase = ToggleSubTaskCompletedUseCase(taskRepository),
            advanceTaskSubTaskUseCase = AdvanceTaskSubTaskUseCase(taskRepository),
            completeCheckHabitUseCase = CompleteCheckHabitUseCase(habitRepository),
            advanceHabitStepUseCase = AdvanceHabitStepUseCase(habitRepository),
            revertHabitStepUseCase = RevertHabitStepUseCase(habitRepository),
            startHabitDurationUseCase = StartHabitDurationUseCase(habitRepository),
            pauseHabitDurationUseCase = PauseHabitDurationUseCase(habitRepository),
            finishHabitDurationUseCase = FinishHabitDurationUseCase(habitRepository),
            undoHabitCompletionUseCase = UndoHabitCompletionUseCase(habitRepository),
            timeProvider = timeProvider,
            quickPreviewPolicy = TodayQuickPreviewPolicy(checkInPolicy),
            remainingTimeFormatter = RemainingTimeFormatter(),
        )
    }

    private fun TestScope.primeUiState(viewModel: TodayViewModel) {
        val collectJob = backgroundScope.launch {
            viewModel.uiState.collect()
        }
        runCurrent()
        collectJob.cancel()
        runCurrent()
    }

    private class FixedTimeProvider(
        private val date: LocalDate,
    ) : TimeProvider {
        override fun nowMillis(): Long = date
            .atTime(12, 0)
            .atZone(zoneId())
            .toInstant()
            .toEpochMilli()

        override fun zoneId(): ZoneId = ZoneId.of("Asia/Singapore")
    }

    private class FakeTaskRepository(
        tasks: List<Task> = emptyList(),
    ) : TaskRepository {
        private val taskFlow = MutableStateFlow(tasks)
        val taskCompleteEvents = mutableListOf<String>()

        override fun observeTasks(includeCompleted: Boolean): Flow<List<Task>> {
            return taskFlow.map { current ->
                if (includeCompleted) current else current.filterNot { it.status == TaskStatus.COMPLETED }
            }
        }

        override suspend fun getTask(taskId: String): Task? = taskFlow.value.firstOrNull { it.id == taskId }

        override suspend fun saveTask(task: Task, subTasks: List<SubTask>) = Unit

        override suspend fun softDeleteTask(taskId: String) = Unit

        override suspend fun setTaskCompleted(taskId: String, completed: Boolean) {
            taskCompleteEvents += "$taskId:$completed"
        }

        override suspend fun setSubTaskCompleted(taskId: String, subTaskId: String, completed: Boolean) = Unit

        override suspend fun advanceTaskSubTask(taskId: String): TaskSubTaskAdvanceResult {
            return TaskSubTaskAdvanceResult()
        }
    }

    private class FakeHabitRepository(
        habits: List<Habit> = emptyList(),
    ) : HabitRepository {
        private val habitFlow = MutableStateFlow(habits)
        val checkCompleteEvents = mutableListOf<String>()

        override fun observeHabits(recordDate: Long, includeDeleted: Boolean): Flow<List<Habit>> = habitFlow

        override suspend fun getHabit(
            habitId: String,
            recordDate: Long,
            includeDeleted: Boolean,
        ): Habit? = habitFlow.value.firstOrNull { it.id == habitId }

        override suspend fun saveHabit(
            habit: Habit,
            steps: List<com.luuzr.jielv.domain.model.HabitStep>,
        ) = Unit

        override suspend fun softDeleteHabit(habitId: String) = Unit

        override suspend fun restoreHabit(habitId: String) = Unit

        override suspend fun completeCheckHabit(habitId: String, recordDate: Long) {
            checkCompleteEvents += habitId
        }

        override suspend fun completeStepsHabit(habitId: String, recordDate: Long) = Unit

        override suspend fun completeDurationHabit(habitId: String, recordDate: Long, durationMinutes: Int) = Unit

        override suspend fun advanceHabitStep(habitId: String, recordDate: Long): HabitStepAdvanceResult {
            return HabitStepAdvanceResult()
        }
    }

    private class FakeNoteRepository : NoteRepository {
        override fun observeNotes(): Flow<List<Note>> = MutableStateFlow(emptyList())

        override suspend fun getNote(noteId: String): NoteDetail? = null

        override suspend fun saveNote(note: Note) = Unit

        override suspend fun softDeleteNote(noteId: String) = Unit

        override suspend fun importImage(noteId: String, sourceUri: String): InsertedNoteImage {
            error("Not used")
        }

        override suspend fun discardDraft(noteId: String) = Unit
    }
}
