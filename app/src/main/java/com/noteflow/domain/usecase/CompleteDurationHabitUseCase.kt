package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.repository.HabitRepository
import javax.inject.Inject

class CompleteDurationHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(
        habitId: String,
        recordDate: Long,
        durationMinutes: Int,
    ) {
        repository.completeDurationHabit(
            habitId = habitId,
            recordDate = recordDate,
            durationMinutes = durationMinutes,
        )
    }
}
