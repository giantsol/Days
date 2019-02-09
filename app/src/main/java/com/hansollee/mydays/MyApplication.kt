package com.hansollee.mydays

import android.app.Application
import android.content.Context

/**
 * Created by kevin-ee on 2019-02-01.
 */

var appContext: Context? = null

class MyApplication : Application() {

    init {
        appContext = this
    }

}