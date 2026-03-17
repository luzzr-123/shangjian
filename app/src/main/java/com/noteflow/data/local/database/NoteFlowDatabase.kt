package com.luuzr.jielv.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.luuzr.jielv.data.local.database.dao.HabitDao
import com.luuzr.jielv.data.local.database.dao.MediaDao
import com.luuzr.jielv.data.local.database.dao.NoteDao
import com.luuzr.jielv.data.local.database.dao.TaskDao
import com.luuzr.jielv.data.local.database.entity.HabitEntity
import com.luuzr.jielv.data.local.database.entity.HabitRecordEntity
import com.luuzr.jielv.data.local.database.entity.HabitStepEntity
import com.luuzr.jielv.data.local.database.entity.MediaEntity
import com.luuzr.jielv.data.local.database.entity.NoteEntity
import com.luuzr.jielv.data.local.database.entity.SubTaskEntity
import com.luuzr.jielv.data.local.database.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        SubTaskEntity::class,
        HabitEntity::class,
        HabitStepEntity::class,
        HabitRecordEntity::class,
        NoteEntity::class,
        MediaEntity::class,
    ],
    version = 4,
    exportSchema = true,
)
abstract class NoteFlowDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao
    abstract fun noteDao(): NoteDao
    abstract fun mediaDao(): MediaDao
}
