package com.hansollee.mydays

import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * Created by kevin-ee on 2019-02-01.
 */

private val timeStringFormatter = DateTimeFormatter.ISO_TIME

fun LocalTime.toStringFormat(): String {
    return this.format(timeStringFormatter)
}

fun String.toLocalTime(): LocalTime {
    return LocalTime.parse(this, timeStringFormatter)
}