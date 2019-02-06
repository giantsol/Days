package com.hansollee.mydays.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hansollee.mydays.models.Task
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-02-01.
 */

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE date LIKE :date ORDER BY from_time ASC, to_time ASC")
    fun getTasksByDate(date: LocalDate): Observable<List<Task>>

    @Query("SELECT * FROM tasks WHERE date BETWEEN :from AND :to")
    fun getTasksBetweenDatesSingle(from: LocalDate, to: LocalDate): Single<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: Task): Completable

    @Update
    fun updateTask(task: Task): Completable

    @Delete
    fun deleteTask(task: Task): Completable
}