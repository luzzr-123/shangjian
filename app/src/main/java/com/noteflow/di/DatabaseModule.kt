package com.luuzr.jielv.di

import android.content.Context
import androidx.room.Room
import com.luuzr.jielv.data.local.database.NoteFlowDatabase
import com.luuzr.jielv.data.local.database.NoteFlowMigrations
import com.luuzr.jielv.data.local.database.dao.HabitDao
import com.luuzr.jielv.data.local.database.dao.MediaDao
import com.luuzr.jielv.data.local.database.dao.NoteDao
import com.luuzr.jielv.data.local.database.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNoteFlowDatabase(
        @ApplicationContext context: Context,
    ): NoteFlowDatabase = Room.databaseBuilder(
        context,
        NoteFlowDatabase::class.java,
        "noteflow.db",
    )
        .addMigrations(NoteFlowMigrations.MIGRATION_1_2)
        .addMigrations(NoteFlowMigrations.MIGRATION_2_3)
        .addMigrations(NoteFlowMigrations.MIGRATION_3_4)
        .build()

    @Provides
    fun provideTaskDao(database: NoteFlowDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideHabitDao(database: NoteFlowDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideNoteDao(database: NoteFlowDatabase): NoteDao = database.noteDao()

    @Provides
    fun provideMediaDao(database: NoteFlowDatabase): MediaDao = database.mediaDao()
}
