package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.model.Habit
import com.luuzr.jielv.domain.repository.HabitRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveHabitsUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    operator fun invoke(
        recordDate: Long,
        includeDeleted: Boolean,
    ): Flow<List<Habit>> {
        return repository.observeHabits(
            recordDate = recordDate,
            includeDeleted = includeDeleted,
        )
    }
}
