package com.hansollee.mydays

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.ResultReceiver
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.hansollee.mydays.models.UniqueTask
import com.hansollee.mydays.tasks.TaskPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener

/**
 * Created by kevin-ee on 2019-02-14.
 */

class SimpleTaskEditDialogActivity: AppCompatActivity(), ColorPickerDialogListener, TaskPickerDialog.Listener {
    companion object {
        const val KEY_TASK_DESCRIPTION = "task.description"
        const val KEY_COLOR = "color"
        const val KEY_RESULT_RECEIVER = "result.receiver"
    }

    private var resultReceiver: ResultReceiver? = null

    private lateinit var taskDescriptionView: EditText
    private lateinit var thumbnail: ImageView

    private var currentThumbnailColor: Int
        get() = (thumbnail.drawable as ColorDrawable).color
        set(color) {
            (thumbnail.drawable.mutate() as ColorDrawable).color = color
        }

    private lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_simple_task_edit_dialog)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        if (intent != null && intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) {
            finish()
            return
        }

        taskDescriptionView = findViewById(R.id.task_description)
        thumbnail = findViewById(R.id.thumbnail)
        val previousTasksButton: View = findViewById(R.id.previous_tasks_button)
        val cancelButton: Button = findViewById(R.id.cancel_button)
        val okButton: Button = findViewById(R.id.ok_button)
        inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        val color = intent.getIntExtra(KEY_COLOR, 0)
        val taskDescription = intent.getStringExtra(KEY_TASK_DESCRIPTION)
        resultReceiver = intent.getParcelableExtra(KEY_RESULT_RECEIVER)

        currentThumbnailColor = color
        taskDescriptionView.setText(taskDescription)

        thumbnail.setOnClickListener { _ ->
            hideKeyboardIfShown()

            ColorPickerDialog.newBuilder()
                .setColor(currentThumbnailColor)
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setShowAlphaSlider(true)
                .create()
                .apply { setColorPickerDialogListener(this@SimpleTaskEditDialogActivity) }
                .show(supportFragmentManager, null)
        }

        previousTasksButton.setOnClickListener { _ ->
            hideKeyboardIfShown()

            TaskPickerDialog()
                .apply { setListener(this@SimpleTaskEditDialogActivity) }
                .show(supportFragmentManager.beginTransaction(), "")
        }

        cancelButton.setOnClickListener { _ ->
            finish()
        }

        okButton.setOnClickListener { _ ->
            val bundle = Bundle()
            bundle.putString(KEY_TASK_DESCRIPTION, taskDescriptionView.text.toString())
            bundle.putInt(KEY_COLOR, currentThumbnailColor)
            resultReceiver?.send(Activity.RESULT_OK, bundle)

            finish()
        }

    }

    private fun hideKeyboardIfShown() {
        if (inputMethodManager.isActive) {
            inputMethodManager.hideSoftInputFromWindow(taskDescriptionView.windowToken, 0)
        }
    }

    override fun onDialogDismissed(dialogId: Int) { }

    override fun onColorSelected(dialogId: Int, color: Int) {
        currentThumbnailColor = color
    }

    override fun onTaskPicked(uniqueTask: UniqueTask) {
        currentThumbnailColor = uniqueTask.colorInt
        taskDescriptionView.setText(uniqueTask.desc)
    }

}
