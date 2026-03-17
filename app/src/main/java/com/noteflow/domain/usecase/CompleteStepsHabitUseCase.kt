package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.repository.HabitRepository
import javax.inject.Inject

class CompleteStepsHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(
        habitId: String,
        recordDate: Long,
    ) {
        repository.completeStepsHabit(
            habitId = habitId,
            recordDate = recordDate,
        )
    }
}
