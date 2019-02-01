package com.hansollee.mydays.models

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

/**
 * Created by kevin-ee on 2019-02-01.
 */

data class Record(val date: LocalDate,
                  val fromTime: LocalTime, val toTime: LocalTime,
                  val task: String) {

}