package com.hansollee.mydays

import android.app.Application
import android.content.Context
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-02-01.
 */

val today = LocalDate.now()
var appContext: Context? = null

class MyApplication : Application() {

    init {
        appContext = this
    }

}