package com.hansollee.mydays

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Created by kevin-ee on 2019-01-31.
 */

private val displayDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

fun Date.toDisplayFormat(): String {
    if (DateUtils.isToday(this.time)) {
        return "Today"
    } else {
        return displayDateFormat.format(this)
    }
}

fun Date.getDateBefore(days: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.DATE, -days)
    return cal.time
}

fun Date.getDateAfter(days: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.DATE, days)
    return cal.time
}
