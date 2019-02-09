package com.hansollee.mydays.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hansollee.mydays.appContext
import com.hansollee.mydays.models.Task

/**
 * Created by kevin-ee on 2019-02-01.
 */

@Database(entities = arrayOf(Task::class), version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        private val INSTANCE by lazy {
            Room.databaseBuilder(appContext, AppDatabase::class.java, "my_days.db")
                .addMigrations(MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
        }

        fun getInstance() = INSTANCE

        // to_time 컬럼을 nonnull -> nullable 타입으로 변경
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                try {
                    database.execSQL("ALTER TABLE tasks RENAME TO _tasks_old;")
                    database.execSQL("DROP INDEX index_tasks_date;")
                    database.execSQL("CREATE TABLE tasks ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        "date INTEGER NOT NULL, " +
                        "from_time INTEGER NOT NULL, " +
                        "to_time INTEGER NULL, " +
                        "task_description TEXT NOT NULL, " +
                        "color_int INTEGER NOT NULL );")
                    database.execSQL("CREATE INDEX index_tasks_date ON tasks (date);")
                    database.execSQL("INSERT INTO tasks (id, date, from_time, to_time, task_description, color_int) " +
                        "SELECT id, date, from_time, to_time, task_description, color_int FROM _tasks_old;")
                    database.setTransactionSuccessful()
                } finally {
                    database.endTransaction()
                }

            }
        }
    }
}