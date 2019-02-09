package com.hansollee.mydays.db

import androidx.room.TypeConverter
import com.hansollee.mydays.LocalDateCompanion
import com.hansollee.mydays.LocalDateTimeCompanion
import com.hansollee.mydays.toEpochSecond
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

/**
 * Created by kevin-ee on 2019-02-01.
 */

class Converters {

    @TypeConverter
    fun dateToSeconds(localDate: LocalDate): Long = localDate.toEpochSecond()
    @TypeConverter
    fun secondsToDate(seconds: Long): LocalDate = LocalDateCompanion.ofEpochSecond(seconds)

    @TypeConverter
    fun dateTimeToSeconds(dateTime: LocalDateTime?): Long? = dateTime?.toEpochSecond()
    @TypeConverter
    fun secondsToDateTime(seconds: Long?): LocalDateTime?
        = seconds?.let { LocalDateTimeCompanion.ofEpochSecond(seconds) }
}