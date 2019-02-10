package com.hansollee.mydays.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hansollee.mydays.models.Task
import com.hansollee.mydays.models.TaskPickerItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-02-01.
 */

@Dao
interface TaskDao {

    @Query("SELECT task_description, color_int, COUNT(*) AS cnt FROM tasks GROUP BY task_description, color_int ORDER BY cnt desc")
    fun getAllTaskPickerItems(): Observable<List<TaskPickerItem>>

    @Query("SELECT * FROM tasks WHERE (start >= :date AND start < :date + 86400) OR " +
        "(start < :date AND end > :date) ORDER BY start ASC, end ASC")
    fun getTasksByDate(date: LocalDate): Single<List<Task>>

    // from, to inclusive
    @Query("SELECT * FROM tasks WHERE (start >= :from AND start < :to + 86400) OR " +
        "(start < :from AND end > :from)")
    fun getTasksBetweenDates(from: LocalDate, to: LocalDate): Single<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: Task): Completable

    @Update
    fun updateTask(task: Task): Completable

    @Delete
    fun deleteTask(task: Task): Completable
}