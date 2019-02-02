package com.hansollee.mydays.models

import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-02-02.
 */

data class History(val date: LocalDate,
                   val records: List<Record>)