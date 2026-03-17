package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.Habit
import com.luuzr.jielv.domain.repository.HabitRepository
import javax.inject.Inject

class GetHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(
        habitId: String,
        recordDate: Long,
        includeDeleted: Boolean = false,
    ): Habit? {
        return repository.getHabit(
            habitId = habitId,
            recordDate = recordDate,
            includeDeleted = includeDeleted,
        )
    }
}
