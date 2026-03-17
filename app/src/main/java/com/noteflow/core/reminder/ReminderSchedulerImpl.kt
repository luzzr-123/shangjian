package com.luuzr.jielv.core.reminder

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.luuzr.jielv.core.time.TimeProvider
import com.luuzr.jielv.data.local.database.dao.HabitDao
import com.luuzr.jielv.data.local.database.dao.TaskDao
import com.luuzr.jielv.data.local.database.entity.HabitEntity
import com.luuzr.jielv.data.local.database.entity.TaskEntity
import com.luuzr.jielv.domain.model.HabitFrequencyType
import com.luuzr.jielv.domain.model.HabitRecordStatus
import com.luuzr.jielv.domain.model.TaskStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Singleton
class ReminderSchedulerImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val habitDao: HabitDao,
    private val workManager: WorkManager,
    private val taskReminderCalculator: TaskReminderCalculator,
    private val habitReminderCalculator: HabitReminderCalculator,
    private val reminderTimeCodec: ReminderTimeCodec,
    private val habitReminderTimeCodec: HabitReminderTimeCodec,
    private val timeProvider: TimeProvider,
) : ReminderScheduler {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun scheduleTask(taskId: String) {
        scheduleTaskInternal(taskId, taskDao.getActiveTaskEntity(taskId))
    }

    override suspend fun cancelTask(taskId: String) {
        workManager.cancelUniqueWork(ReminderConstants.uniqueWorkName(taskId))
    }

    override suspend fun scheduleHabit(habitId: String) {
        scheduleHabitInternal(habitId, habitDao.getActiveHabitEntity(habitId))
    }

    override suspend fun cancelHabit(habitId: String) {
        workManager.cancelUniqueWork(ReminderConstants.uniqueHabitWorkName(habitId))
    }

    override suspend fun rescheduleAllActiveReminders() {
        taskDao.getActiveTaskEntities().forEach { task ->
            scheduleTaskInternal(task.id, task)
        }
        habitDao.getActiveHabitEntities().forEach { habit ->
            scheduleHabitInternal(habit.id, habit)
        }
    }

    private suspend fun scheduleTaskInternal(
        taskId: String,
        task: TaskEntity?,
    ) {
        if (
            task == null ||
            task.status == TaskStatus.COMPLETED.name ||
            task.isDeleted ||
            (task.dueAt != null && task.dueAt < timeProvider.nowMillis())
        ) {
            cancelTask(taskId)
            return
        }
        val now = timeProvider.nowMillis()
        val occurrence = taskReminderCalculator.calculateNext(
            spec = task.toReminderSpec(),
            nowMillis = now,
        ) ?: run {
            cancelTask(taskId)
            return
        }
        enqueueTask(task.id, occurrence.atMillis, occurrence.reason, now)
    }

    private suspend fun scheduleHabitInternal(
        habitId: String,
        habit: HabitEntity?,
    ) {
        if (habit == null || habit.isDeleted) {
            cancelHabit(habitId)
            return
        }
        val now = timeProvider.nowMillis()
        val currentDate = timeProvider.currentDate()
        val completedEpochDays = buildList {
            add(currentDate)
            if (habit.hasCrossMidnightReminderWindow()) {
                add(currentDate.minusDays(1))
            }
        }
            .filter { date ->
                habitDao.getHabitRecordForDate(habitId, date.toEpochDay())?.status ==
                    HabitRecordStatus.COMPLETED.name
            }
            .mapTo(linkedSetOf()) { it.toEpochDay() }
        val spec = habit.toReminderSpec(completedEpochDays)
        val occurrence = habitReminderCalculator.calculateNext(spec, now) ?: run {
            cancelHabit(habitId)
            return
        }
        enqueueHabit(habit.id, occurrence.atMillis, occurrence.reason, now)
    }

    private fun enqueueTask(
        taskId: String,
        triggerAtMillis: Long,
        reason: ReminderTriggerReason,
        nowMillis: Long,
    ) {
        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(max(0L, triggerAtMillis - nowMillis), TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    ReminderConstants.workerTaskIdKey to taskId,
                    ReminderConstants.workerTriggerAtKey to triggerAtMillis,
                    ReminderConstants.workerTriggerReasonKey to reason.name,
                ),
            )
            .addTag(ReminderConstants.uniqueWorkName(taskId))
            .build()
        workManager.enqueueUniqueWork(
            ReminderConstants.uniqueWorkName(taskId),
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    private fun enqueueHabit(
        habitId: String,
        triggerAtMillis: Long,
        reason: ReminderTriggerReason,
        nowMillis: Long,
    ) {
        val request = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInitialDelay(max(0L, triggerAtMillis - nowMillis), TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    ReminderConstants.workerHabitIdKey to habitId,
                    ReminderConstants.workerTriggerAtKey to triggerAtMillis,
                    ReminderConstants.workerTriggerReasonKey to reason.name,
                ),
            )
            .addTag(ReminderConstants.uniqueHabitWorkName(habitId))
            .build()
        workManager.enqueueUniqueWork(
            ReminderConstants.uniqueHabitWorkName(habitId),
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    private fun TaskEntity.toReminderSpec(): TaskReminderSpec {
        return TaskReminderSpec(
            taskId = id,
            title = title,
            isCompleted = status == TaskStatus.COMPLETED.name,
            isDeleted = isDeleted,
            archived = archived,
            startReminderMinuteOfDay = startReminderMinuteOfDay,
            windowEndMinuteOfDay = windowEndMinuteOfDay,
            dueAt = dueAt,
            repeatIntervalMinutes = repeatIntervalMinutes,
            exactReminderTimes = reminderTimeCodec.decode(exactReminderTimesJson),
            allDay = allDay,
        )
    }

    private fun HabitEntity.toReminderSpec(completedEpochDays: Set<Long>): HabitReminderSpec {
        val frequencyValue = decodeFrequencyValue(frequencyValueJson)
        return HabitReminderSpec(
            habitId = id,
            title = title,
            frequencyType = frequencyType.toHabitFrequencyType(),
            selectedWeekdays = frequencyValue.weekdays
                .mapNotNull { value -> runCatching { DayOfWeek.of(value) }.getOrNull() }
                .toSet(),
            intervalDays = frequencyValue.intervalDays,
            intervalAnchorDate = frequencyValue.anchorDate?.let(LocalDate::parse),
            monthlyDays = frequencyValue.daysOfMonth.toSet(),
            remindWindowStart = remindWindowStart?.let(LocalTime::parse),
            remindWindowEnd = remindWindowEnd?.let(LocalTime::parse),
            repeatIntervalMinutes = repeatIntervalMinutes,
            exactReminderTimes = habitReminderTimeCodec.decode(exactReminderTimesJson),
            isDeleted = isDeleted,
            archived = archived,
            completedEpochDays = completedEpochDays,
        )
    }

    private fun HabitEntity.hasCrossMidnightReminderWindow(): Boolean {
        return reminderWindowCrossesMidnight(
            windowStart = remindWindowStart?.let(LocalTime::parse),
            windowEnd = remindWindowEnd?.let(LocalTime::parse),
        )
    }

    private fun decodeFrequencyValue(rawValue: String?): FrequencyValuePayload {
        if (rawValue.isNullOrBlank()) return FrequencyValuePayload()
        return runCatching { json.decodeFromString<FrequencyValuePayload>(rawValue) }
            .getOrDefault(FrequencyValuePayload())
    }

    private fun String.toHabitFrequencyType(): HabitFrequencyType {
        return HabitFrequencyType.entries.firstOrNull { it.name == this } ?: HabitFrequencyType.DAILY
    }

    @Serializable
    private data class FrequencyValuePayload(
        val weekdays: List<Int> = emptyList(),
        val intervalDays: Int? = null,
        val anchorDate: String? = null,
        val daysOfMonth: List<Int> = emptyList(),
    )
}
