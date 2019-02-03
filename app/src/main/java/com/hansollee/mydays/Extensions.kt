package com.hansollee.mydays

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.threeten.bp.format.SignStyle
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoField

/**
 * Created by kevin-ee on 2019-02-02.
 */

private val dateStringFormatter = DateTimeFormatterBuilder()
    .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
    .appendLiteral('-')
    .appendValue(ChronoField.MONTH_OF_YEAR, 2)
    .appendLiteral('-')
    .appendValue(ChronoField.DAY_OF_MONTH, 2)
    .appendLiteral(' ')
    .appendText(ChronoField.DAY_OF_WEEK, TextStyle.SHORT_STANDALONE)
    .toFormatter()
fun LocalDate.toStringFormat(): String {
    return if (this == today) {
        "${this.format(dateStringFormatter)} (Today)"
    } else {
        this.format(dateStringFormatter)
    }
}

private val timeStringFormatter = DateTimeFormatterBuilder()
    .appendValue(ChronoField.HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .toFormatter()
fun LocalTime.toStringFormat(): String {
    return this.format(timeStringFormatter)
}

private val MINUTES_PER_HOUR = 60
fun LocalTime.toMinuteOfDay(): Int = hour * MINUTES_PER_HOUR + minute
