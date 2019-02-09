package com.hansollee.mydays

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
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

private val timeStringFormatter = DateTimeFormatterBuilder()
    .appendValue(ChronoField.HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .toFormatter()

fun LocalTime.toMinuteOfDay(): Int = hour * MINUTES_PER_HOUR + minute

// LocalDateTime

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
        LocalTime.MAX
    }
}

fun LocalDateTime.toEndTimeDisplayFormat(date: LocalDate): String = toEndTime(date).format(timeStringFormatter)
