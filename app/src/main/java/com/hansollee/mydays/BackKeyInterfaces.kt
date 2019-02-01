package com.hansollee.mydays

/**
 * Created by kevin-ee on 2019-02-01.
 */

interface BackKeyDispatcher {
    fun addBackKeyListener(listener: BackKeyListener)
    fun removeBackKeyListener(listener: BackKeyListener)
    fun dispatchBackKey(): Boolean
}

interface BackKeyListener {
    fun onBackPressed(): Boolean
}

