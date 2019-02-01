package com.hansollee.mydays.models

/**
 * Created by kevin-ee on 2019-02-01.
 */

data class Time(val hours: Int, val minutes: Int, val isAm: Boolean) {

    companion object {
        private const val MINUTE = 60 * 1000
        private const val HOUR = 60 * MINUTE
    }

    val millis: Long
        get() = if (isAm) {
            hours * HOUR + minutes * MINUTE
        } else {
            (hours + 12) * HOUR + minutes * MINUTE
        }.toLong()


    fun minus(anotherTime: Time): Long = this.millis - anotherTime.millis
}

