package com.hansollee.mydays

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

/**
 * Created by kevin-ee on 2019-01-31.
 */

private val dateStringFormatter = DateTimeFormatter.ISO_DATE

fun LocalDate.toStringFormat(): String {
    return this.format(dateStringFormatter)
}
