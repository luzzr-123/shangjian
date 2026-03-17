package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.repository.HabitRepository
import javax.inject.Inject

class HardDeleteHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
) {
    suspend operator fun invoke(habitId: String) {
        habitRepository.hardDeleteHabit(habitId)
    }
}
