package com.hansollee.mydays

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.threeten.bp.temporal.ChronoField

/**
 * Created by kevin-ee on 2019-02-02.
 */

private val dateStringFormatter = DateTimeFormatter.ISO_DATE
fun LocalDate.toStringFormat(): String {
    return this.format(dateStringFormatter)
}

private val timeStringFormatter = DateTimeFormatterBuilder()
    .appendValue(ChronoField.HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .toFormatter()
fun LocalTime.toStringFormat(): String {
    return this.format(timeStringFormatter)
}
