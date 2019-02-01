package com.hansollee.mydays.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hansollee.mydays.appContext
import com.hansollee.mydays.models.Record

/**
 * Created by kevin-ee on 2019-02-01.
 */

@Database(entities = arrayOf(Record::class), version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao

    companion object {
        private val INSTANCE by lazy {
            Room.databaseBuilder(appContext, AppDatabase::class.java, "mydays.db")
                .fallbackToDestructiveMigration()
                .build()
        }

        fun getInstance() = INSTANCE
    }
}