package com.hansollee.mydays

import android.content.Context

/**
 * Created by kevin-ee on 2019-02-07.
 */

class AppSharedPrefs {

    companion object {

        private const val PREF_KEY = "mydays.preferences"

        private val INSTANCE by lazy {
            AppSharedPrefs()
        }
        fun getInstance() = INSTANCE
    }

    private val pref = appContext!!.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
}