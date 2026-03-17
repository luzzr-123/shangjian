package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.Habit
import com.luuzr.jielv.domain.model.HabitStep
import com.luuzr.jielv.domain.repository.HabitRepository
import javax.inject.Inject

class SaveHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(
        habit: Habit,
        steps: List<HabitStep>,
    ) {
        repository.saveHabit(habit = habit, steps = steps)
    }
}
