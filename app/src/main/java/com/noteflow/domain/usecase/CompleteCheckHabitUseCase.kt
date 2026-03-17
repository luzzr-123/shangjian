package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.repository.HabitRepository
import javax.inject.Inject

class CompleteCheckHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(
        habitId: String,
        recordDate: Long,
    ) {
        repository.completeCheckHabit(
            habitId = habitId,
            recordDate = recordDate,
        )
    }
}
