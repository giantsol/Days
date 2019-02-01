package com.hansollee.mydays

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

/**
 * Created by kevin-ee on 2019-01-31.
 */

private val dateDisplayFormatter = DateTimeFormatter.ISO_DATE

fun LocalDate.toDisplayFormat(): String {
    val displayFormat = this.format(dateDisplayFormatter)
    return if (this == LocalDate.now()) {
        "$displayFormat (Today)"
    } else {
        displayFormat
    }
}
