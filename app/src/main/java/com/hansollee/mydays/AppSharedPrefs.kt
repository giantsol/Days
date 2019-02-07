package com.hansollee.mydays

import android.content.Context

/**
 * Created by kevin-ee on 2019-02-07.
 */

class AppSharedPrefs {

    companion object {

        private const val PREF_KEY = "mydays.preferences"
        private const val KEY_USER_TOUCHED_START_TIME_DESC = "user.touched.start.time.desc"

        private val INSTANCE by lazy {
            AppSharedPrefs()
        }
        fun getInstance() = INSTANCE
    }

    private val pref = appContext!!.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)

    fun hasUserTouchedStartTimeDesc(): Boolean = pref.getBoolean(KEY_USER_TOUCHED_START_TIME_DESC, false)

    fun setHasUserTouchedStartTimeDesc(value: Boolean) = pref.edit().putBoolean(KEY_USER_TOUCHED_START_TIME_DESC, value).apply()
}