package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.HabitStepAdvanceResult
import com.luuzr.jielv.domain.repository.HabitRepository
import javax.inject.Inject

class RevertHabitStepUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(
        habitId: String,
        recordDate: Long,
        stepId: String,
    ): HabitStepAdvanceResult {
        return repository.revertHabitStep(
            habitId = habitId,
            recordDate = recordDate,
            stepId = stepId,
        )
    }
}
