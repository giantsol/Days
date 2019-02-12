package com.hansollee.mydays.models

import androidx.room.ColumnInfo

/**
 * Created by kevin-ee on 2019-02-06.
 */

data class UniqueTask(@ColumnInfo(name = "task_description") val desc: String,
                      @ColumnInfo(name = "color_int") val colorInt: Int)
