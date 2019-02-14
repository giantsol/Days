package com.hansollee.mydays

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.ResultReceiver
import androidx.appcompat.app.AppCompatActivity
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener

/**
 * Created by kevin-ee on 2019-02-14.
 */

class SimpleTaskEditActivity: AppCompatActivity(), ColorPickerDialogListener {
    companion object {
        const val KEY_CURRENT_TASK_DESCRIPTION = "current.task.description"
        const val KEY_CURRENT_COLOR = "current.color"
        const val KEY_RESULT_RECEIVER = "result.receiver"

        const val KEY_EDITED_TASK_DESCRIPTION = "edited.task.description"
        const val KEY_EDITED_COLOR = "edited.color"
    }

    private var resultReceiver: ResultReceiver? = null
    private var editedColor = Color.TRANSPARENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent != null && intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) {
            finish()
            return
        }

        val currentColor = intent.getIntExtra(KEY_CURRENT_COLOR, 0)
        resultReceiver = intent.getParcelableExtra(KEY_RESULT_RECEIVER)

//        ColorPickerDialog.newBuilder()
//            .setColor(currentColor)
//            .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
//            .setShowAlphaSlider(true)
//            .create()
//            .also {
//                it.setColorPickerDialogListener(this@ColorPickerDialogFragmentActivity)
//            }
//            .show(supportFragmentManager, "")

    }

    override fun onDialogDismissed(dialogId: Int) {
        finish()
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        editedColor = color
    }

    override fun onDestroy() {
        super.onDestroy()

        if (editedColor != Color.TRANSPARENT) {
            val bundle = Bundle()
            bundle.putInt(KEY_EDITED_COLOR, editedColor)
            resultReceiver?.send(Activity.RESULT_OK, bundle)
        } else {
            resultReceiver?.send(Activity.RESULT_CANCELED, null)
        }
    }
}
