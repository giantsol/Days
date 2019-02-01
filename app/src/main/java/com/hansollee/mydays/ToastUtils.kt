package com.hansollee.mydays

import android.widget.Toast

/**
 * Created by kevin-ee on 2019-02-01.
 */

fun toast(msg: String?) {
    Toast.makeText(appContext, msg ?: "No Message", Toast.LENGTH_SHORT).show()
}
