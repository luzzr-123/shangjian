package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.HabitStepAdvanceResult
import com.luuzr.jielv.domain.repository.HabitRepository
import javax.inject.Inject

class AdvanceHabitStepUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(
        habitId: String,
        recordDate: Long,
    ): HabitStepAdvanceResult {
        return repository.advanceHabitStep(
            habitId = habitId,
            recordDate = recordDate,
        )
    }
}
