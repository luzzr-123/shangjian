package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.HabitDurationFinishResult
import com.luuzr.jielv.domain.repository.HabitRepository
import javax.inject.Inject

class FinishHabitDurationUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(
        habitId: String,
        recordDate: Long,
    ): HabitDurationFinishResult {
        return repository.finishHabitDuration(
            habitId = habitId,
            recordDate = recordDate,
        )
    }
}
