package com.luuzr.jielv.data.local.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.luuzr.jielv.data.local.database.entity.HabitEntity
import com.luuzr.jielv.data.local.database.entity.HabitStepEntity

data class HabitWithSteps(
    @Embedded val habit: HabitEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "habitId",
    )
    val steps: List<HabitStepEntity>,
)
