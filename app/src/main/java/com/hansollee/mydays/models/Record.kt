package com.hansollee.mydays.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

/**
 * Created by kevin-ee on 2019-02-01.
 */

@Entity(tableName = "records",
    indices = arrayOf(Index(value = ["date"])))
data class Record(@ColumnInfo(name = "date") val date: LocalDate,
                  @ColumnInfo(name = "from_time") val fromTime: LocalTime,
                  @ColumnInfo(name = "to_time") val toTime: LocalTime,
                  @ColumnInfo(name = "task_description") val task: String) {

    @PrimaryKey(autoGenerate = true) var id = 0
}