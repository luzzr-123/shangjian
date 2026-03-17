package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.core.time.TimeProvider
import com.luuzr.jielv.domain.model.TrashItem
import com.luuzr.jielv.domain.model.TrashItemType
import com.luuzr.jielv.domain.repository.HabitRepository
import com.luuzr.jielv.domain.repository.NoteRepository
import com.luuzr.jielv.domain.repository.TaskRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveTrashItemsUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository,
    private val noteRepository: NoteRepository,
    private val timeProvider: TimeProvider,
) {
    operator fun invoke(): Flow<List<TrashItem>> {
        val recordDate = timeProvider.currentDate().toEpochDay()
        return combine(
            taskRepository.observeDeletedTasks(),
            habitRepository.observeDeletedHabits(recordDate),
            noteRepository.observeDeletedNotes(),
        ) { tasks, habits, notes ->
            buildList {
                addAll(
                    tasks.map { task ->
                        TrashItem(
                            id = task.id,
                            type = TrashItemType.TASK,
                            title = task.title,
                            deletedAt = task.deletedAt ?: task.updatedAt,
                            previewText = task.contentMarkdown.orEmpty().ifBlank { "已删除任务" },
                        )
                    },
                )
                addAll(
                    habits.map { habit ->
                        TrashItem(
                            id = habit.id,
                            type = TrashItemType.HABIT,
                            title = habit.title,
                            deletedAt = habit.deletedAt ?: habit.updatedAt,
                            previewText = habit.contentMarkdown.orEmpty().ifBlank { "已删除习惯" },
                        )
                    },
                )
                addAll(
                    notes.map { note ->
                        TrashItem(
                            id = note.id,
                            type = TrashItemType.NOTE,
                            title = note.title,
                            deletedAt = note.deletedAt ?: note.updatedAt,
                            previewText = note.previewText.orEmpty().ifBlank { "已删除笔记" },
                        )
                    },
                )
            }.sortedByDescending { it.deletedAt }
        }
    }
}
