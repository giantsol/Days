package com.hansollee.mydays.db

import androidx.room.TypeConverter
import com.hansollee.mydays.toLocalDate
import com.hansollee.mydays.toLocalTime
import com.hansollee.mydays.toStringFormat
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

/**
 * Created by kevin-ee on 2019-02-01.
 */

class Converters {
    @TypeConverter
    fun dateToString(localDate: LocalDate): String = localDate.toStringFormat()
    @TypeConverter
    fun stringToDate(s: String): LocalDate = s.toLocalDate()

    @TypeConverter
    fun timeToString(localTime: LocalTime): String = localTime.toStringFormat()
    @TypeConverter
    fun stringToTime(s: String): LocalTime = s.toLocalTime()
}