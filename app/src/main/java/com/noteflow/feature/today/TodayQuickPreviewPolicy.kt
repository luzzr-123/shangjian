package com.luuzr.jielv.feature.today

import com.luuzr.jielv.domain.model.Habit
import com.luuzr.jielv.domain.model.HabitTodayStatus
import com.luuzr.jielv.domain.model.Task
import com.luuzr.jielv.domain.usecase.CheckInPolicy
import com.luuzr.jielv.domain.usecase.HabitQuickActionState
import com.luuzr.jielv.domain.usecase.TaskQuickActionState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodayQuickPreviewPolicy @Inject constructor(
    private val checkInPolicy: CheckInPolicy,
) {

    fun evaluateTask(
        task: Task,
        nowMillis: Long,
    ): TaskQuickActionState {
        return checkInPolicy.evaluateTaskQuickAction(
            task = task,
            nowMillis = nowMillis,
        )
    }

    fun evaluateHabit(
        habit: Habit,
        todayStatus: HabitTodayStatus,
        nowMillis: Long,
    ): HabitQuickActionState {
        return checkInPolicy.evaluateHabitQuickAction(
            habit = habit,
            todayStatus = todayStatus,
            nowMillis = nowMillis,
        )
    }
}
