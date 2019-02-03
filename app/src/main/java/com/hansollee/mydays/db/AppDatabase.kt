package com.hansollee.mydays.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hansollee.mydays.appContext
import com.hansollee.mydays.models.Task

/**
 * Created by kevin-ee on 2019-02-01.
 */

@Database(entities = arrayOf(Task::class), version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        private val INSTANCE by lazy {
            Room.databaseBuilder(appContext, AppDatabase::class.java, "my_days.db")
                .fallbackToDestructiveMigration()
                .build()
        }

        fun getInstance() = INSTANCE
    }
}