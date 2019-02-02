package com.hansollee.mydays.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hansollee.mydays.models.Record
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-02-01.
 */

@Dao
interface RecordDao {
    @Query("SELECT * FROM records WHERE date LIKE :date ORDER BY from_time ASC, to_time ASC")
    fun getRecordsByDate(date: LocalDate): Observable<List<Record>>

    @Query("SELECT * FROM records WHERE date BETWEEN :from AND :to")
    fun getRecordsBetweenDates(from: LocalDate, to: LocalDate): Single<List<Record>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecord(record: Record): Completable

    @Update
    fun updateRecord(record: Record): Completable

    @Delete
    fun deleteRecord(record: Record): Completable
}