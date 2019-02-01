package com.hansollee.mydays.models

import java.util.Date

/**
 * Created by kevin-ee on 2019-02-01.
 */

data class Record(val date: Date,
                  val fromTime: Time, val toTime: Time,
                  val task: String) {

}