package com.hansollee.mydays

import com.hansollee.mydays.models.Task
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.threeten.bp.format.SignStyle
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoField

/**
 * Created by kevin-ee on 2019-02-02.
 */

val SECONDS_PER_DAY = 86400
private val MINUTES_PER_HOUR = 60

private val todayText by lazy {
    appContext!!.resources.getString(R.string.today_text)
}

val proceedingText by lazy {
    appContext!!.getString(R.string.text_proceeding)
}

private val zoneOffset by lazy {
    OffsetDateTime.now().offset
}

// LocalDate

private val dateStringFormatter by lazy {
    DateTimeFormatterBuilder()
        .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
        .appendLiteral('-')
        .appendValue(ChronoField.MONTH_OF_YEAR, 2)
        .appendLiteral('-')
        .appendValue(ChronoField.DAY_OF_MONTH, 2)
        .appendLiteral(' ')
        .appendText(ChronoField.DAY_OF_WEEK, TextStyle.SHORT_STANDALONE)
        .toFormatter()
}
private val dateStringParser by lazy {
    DateTimeFormatterBuilder()
        .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
        .appendLiteral('-')
        .appendValue(ChronoField.MONTH_OF_YEAR, 2)
        .appendLiteral('-')
        .appendValue(ChronoField.DAY_OF_MONTH, 2)
        .appendLiteral(' ')
        .appendText(ChronoField.DAY_OF_WEEK, TextStyle.SHORT_STANDALONE)
        .optionalStart()
        .appendLiteral(" ($todayText)")
        .toFormatter()
}

fun LocalDate.toDisplayFormat(today: LocalDate): String {
    return if (this == today) {
        "${this.format(dateStringFormatter)} ($todayText)"
    } else {
        this.format(dateStringFormatter)
    }
}

fun String.toLocalDate(): LocalDate {
    return LocalDate.parse(this, dateStringParser)
}

fun LocalDate.toEpochSecond(): Long {
    val seconds = this.toEpochDay() * SECONDS_PER_DAY
    return seconds - zoneOffset.totalSeconds
}

object LocalDateCompanion {
    fun ofEpochSecond(seconds: Long): LocalDate {
        val localSecond = seconds + zoneOffset.totalSeconds
        return LocalDate.ofEpochDay(localSecond % SECONDS_PER_DAY)
    }
}

// LocalTime

private val MAX_LOCALTIME = LocalTime.of(23, 59, 59)

private val hoursText by lazy {
    appContext!!.resources.getString(R.string.hours_text)
}

private val minutesText by lazy {
    appContext!!.resources.getString(R.string.minutes_text)
}

private val timeStringFormatter = DateTimeFormatterBuilder()
    .appendValue(ChronoField.HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .toFormatter()

private val durationStringFormatter = DateTimeFormatterBuilder()
    .appendValue(ChronoField.HOUR_OF_DAY)
    .appendLiteral("$hoursText ")
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .appendLiteral(minutesText)
    .toFormatter()

fun LocalTime.toMinuteOfDay(): Int = if (this == MAX_LOCALTIME) 24 * MINUTES_PER_HOUR else hour * MINUTES_PER_HOUR + minute

// LocalDateTime

private val dateTimeStringFormatter = DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral(' ')
    .appendValue(ChronoField.HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .toFormatter()

fun LocalDateTime.toEpochSecond(): Long = this.toEpochSecond(zoneOffset)

object LocalDateTimeCompanion {
    fun ofEpochSecond(seconds: Long): LocalDateTime = LocalDateTime.ofEpochSecond(seconds, 0, zoneOffset)
}

fun LocalDateTime.toStartTime(date: LocalDate): LocalTime {
    val thisDate = this.toLocalDate()
    return if (thisDate == date) {
        this.toLocalTime()
    } else {
        LocalTime.MIN
    }
}

fun LocalDateTime.toStartTimeDisplayFormat(date: LocalDate): String = toStartTime(date).format(timeStringFormatter)

fun LocalDateTime.toEndTime(date: LocalDate): LocalTime {
    val thisDate = this.toLocalDate()
    return if (thisDate == date) {
        this.toLocalTime()
    } else {
        MAX_LOCALTIME
    }
}

fun LocalDateTime.toEndTimeDisplayFormat(date: LocalDate): String {
    val endTime = this.toEndTime(date)
    return if (endTime == MAX_LOCALTIME) {
        "24:00"
    } else {
        endTime.format(timeStringFormatter)
    }
}

fun LocalDateTime.toDisplayFormat(): String = this.format(dateTimeStringFormatter)

fun List<Task>.getTotalDurationString(date: LocalDate): String {
    var totalMinutes = 0L
    var hasProceedingTask = false
    for (task in this) {
        val startTime = task.startDateTime.toStartTime(date)
        val endTime = task.endDateTime?.toEndTime(date)

        if (endTime != null) {
            totalMinutes += endTime.toMinuteOfDay() - startTime.toMinuteOfDay()
        } else {
            hasProceedingTask = true
        }
    }

    val durationString = LocalTime.of((totalMinutes / MINUTES_PER_HOUR).toInt(), (totalMinutes % MINUTES_PER_HOUR).toInt()).format(durationStringFormatter)
    return if (!hasProceedingTask) {
        durationString
    } else {
        "$durationString + $proceedingText"
    }

}
