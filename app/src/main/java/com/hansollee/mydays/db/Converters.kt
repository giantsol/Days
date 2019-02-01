package com.hansollee.mydays.db

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

/**
 * Created by kevin-ee on 2019-02-01.
 */

class Converters {
    @TypeConverter
    fun dateToEpoch(localDate: LocalDate): Long = localDate.toEpochDay()
    @TypeConverter
    fun epochToDate(epoch: Long): LocalDate = LocalDate.ofEpochDay(epoch)

    @TypeConverter
    fun timeToSeconds(localTime: LocalTime): Int = localTime.toSecondOfDay()
    @TypeConverter
    fun secondsToTime(seconds: Int): LocalTime = LocalTime.ofSecondOfDay(seconds.toLong())
}