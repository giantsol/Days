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
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-02-01.
 */

@Dao
interface RecordDao {
    @Query("SELECT * FROM records WHERE date LIKE :date ORDER BY from_time ASC, to_time ASC")
    fun getRecordsByDate(date: LocalDate): Observable<List<Record>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertRecord(record: Record): Completable

    @Update
    fun updateRecord(record: Record): Completable

    @Delete
    fun deleteRecord(record: Record): Completable
}