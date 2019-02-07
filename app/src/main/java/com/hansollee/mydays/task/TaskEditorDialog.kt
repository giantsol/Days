package com.hansollee.mydays.task

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.hansollee.mydays.R
import com.hansollee.mydays.models.Task
import com.hansollee.mydays.models.TaskDescription
import com.hansollee.mydays.toast
import com.hansollee.mydays.widgets.SimpleTimePicker
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener

/**
 * Created by kevin-ee on 2019-02-01.
 */

class TaskEditorDialog : DialogFragment(), ColorPickerDialogListener, TaskDescPickerDialog.Listener {

    private data class ValidityCheckResult(val isOk: Boolean, val errorMessage: String?)

    companion object {
        private const val KEY_TASK = "key.task"
        private const val KEY_THUMBNAIL_COLOR = "key.thumbnail.color"

        private const val TAG_COLOR_PICKER = "ColorPicker"
        private const val TAG_TASK_DESC_PICKER = "TaskDescPicker"

        fun newInstance(task: Task? = null): TaskEditorDialog {
            val instance = TaskEditorDialog()

            val args = Bundle()
            args.putParcelable(KEY_TASK, task)
            instance.arguments = args

            return instance
        }
    }

    private lateinit var taskFragViewModel: TaskFragmentViewModel
    private lateinit var fromTimePicker: SimpleTimePicker
    private lateinit var toTimePicker: SimpleTimePicker
    private lateinit var taskText: EditText
    private lateinit var thumbnail: ImageView

    // task를 클릭해서 열렸으면 Nonnull, 새로만들기 버튼을 클릭해서 열렸으면 null
    private var originalTask: Task? = null

    private lateinit var fromToInvalidMsg: String
    private lateinit var taskDescriptionInvalidMsg: String

    private val currentThumbnailColor: Int
        get() = (thumbnail.drawable as ColorDrawable).color

    private lateinit var inputMethodManager: InputMethodManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        originalTask = arguments.getParcelable(KEY_TASK)

        val view = inflater.inflate(R.layout.dialog_task_editor, container, false)
        taskFragViewModel = ViewModelProviders.of(activity).get(TaskFragmentViewModel::class.java)
        val title: TextView = view.findViewById(R.id.title)
        val startTimeDescView: TextView = view.findViewById(R.id.start_time_desc)
        val endTimeDescView: TextView = view.findViewById(R.id.end_time_desc)
        fromTimePicker = view.findViewById(R.id.start_timepicker)
        toTimePicker = view.findViewById(R.id.end_timepicker)
        taskText = view.findViewById(R.id.task_input)
        thumbnail = view.findViewById(R.id.thumbnail)
        val copyButton: Button = view.findViewById(R.id.copy_from_previous_tasks)
        val cancelButton: Button = view.findViewById(R.id.cancel_button)
        val deleteButton: Button = view.findViewById(R.id.delete_button)
        val okButton: Button = view.findViewById(R.id.ok_button)

        inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        val res = context.resources
        fromToInvalidMsg = res.getString(R.string.from_and_to_invalid)
        taskDescriptionInvalidMsg = res.getString(R.string.task_description_invalid)
        val createNewTaskTitle = res.getString(R.string.create_new_task_title)
        val editTaskTitle = res.getString(R.string.edit_task_title)
        val startTimeDesc = res.getString(R.string.start_time_desc)
        val endTimeDesc = res.getString(R.string.end_time_desc)

        title.text = if (originalTask == null) createNewTaskTitle else editTaskTitle
        startTimeDescView.text = startTimeDesc
        endTimeDescView.text = endTimeDesc

        fromTimePicker.setOnTimeChangedListener(object: SimpleTimePicker.OnTimeChangedListener {
            override fun onTimeChanged(hourOfDay: Int, minute: Int) {
                if (toTimePicker < fromTimePicker) {
                    toTimePicker.setTime(hourOfDay, minute)
                }
            }
        })

        (fragmentManager.findFragmentByTag(TAG_COLOR_PICKER) as? ColorPickerDialog)
            ?.setColorPickerDialogListener(this)

        thumbnail.setOnClickListener { _ ->
            hideKeyboardIfShown()

            ColorPickerDialog.newBuilder()
                .setColor(currentThumbnailColor)
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setShowAlphaSlider(true)
                .create()
                .apply { setColorPickerDialogListener(this@TaskEditorDialog) }
                .show(fragmentManager, TAG_COLOR_PICKER)
        }

        (fragmentManager.findFragmentByTag(TAG_TASK_DESC_PICKER) as? TaskDescPickerDialog)
            ?.setListener(this)

        copyButton.setOnClickListener { _ ->
            TaskDescPickerDialog().apply { setListener(this@TaskEditorDialog) }
                .show(fragmentManager.beginTransaction(), TAG_TASK_DESC_PICKER)
        }

        cancelButton.setOnClickListener { _ ->
            dismiss()
        }

        if (originalTask != null) {
            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener { _ ->
                taskFragViewModel.deleteTask(originalTask!!)
                dismiss()
            }
        } else {
            deleteButton.visibility = View.GONE
            deleteButton.setOnClickListener(null)
        }

        okButton.setOnClickListener { _ ->
            // 우선 input들이 모두 valid한지 체크
            val validityCheckResult = getValidityCheckResult()
            if (validityCheckResult.isOk) {
                if (originalTask == null) {
                    taskFragViewModel.insertNewTask(createNewTaskFromInputs())
                } else {
                    taskFragViewModel.updateTask(getUpdatedTaskWithInputs(originalTask!!))
                }
                dismiss()
            } else {
                toast(validityCheckResult.errorMessage)
            }
        }

        if (originalTask != null) {
            fillViewsWithTask(originalTask!!)
        } else {
            taskFragViewModel.getCurrentTasks().value.lastOrNull()?.also { lastTask ->
                fromTimePicker.setTime(lastTask.toTime)
                toTimePicker.setTime(lastTask.toTime)
            }
        }

        if (savedInstanceState != null) {
            updateThumbnailColor(savedInstanceState.getInt(KEY_THUMBNAIL_COLOR), taskFragViewModel.defaultTaskColor)
        }

        return view
    }

    private fun getValidityCheckResult(): ValidityCheckResult {
        val fromTime = fromTimePicker.time
        val toTime = toTimePicker.time
        if (fromTime > toTime) {
            return ValidityCheckResult(false, fromToInvalidMsg)
        }

        if (taskText.text.trim().isEmpty()) {
            return ValidityCheckResult(false, taskDescriptionInvalidMsg)
        }

        return ValidityCheckResult(true, null)
    }

    private fun createNewTaskFromInputs(): Task {
        val date = originalTask?.date ?: taskFragViewModel.getCurrentDate().value
        return Task(date, fromTimePicker.time, toTimePicker.time, taskText.text.toString(),
            (thumbnail.drawable as ColorDrawable).color)
    }

    private fun getUpdatedTaskWithInputs(originalTask: Task): Task
        = createNewTaskFromInputs().also { it.id = originalTask.id }

    private fun fillViewsWithTask(task: Task) {
        val fromTime = task.fromTime
        val toTime = task.toTime

        fromTimePicker.setTime(fromTime)
        toTimePicker.setTime(toTime)

        taskText.setText(task.desc)
        updateThumbnailColor(task.colorInt, taskFragViewModel.defaultTaskColor)
    }

    override fun onStart() {
        super.onStart()
        dialog.also {
            it.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setCanceledOnTouchOutside(false)
        }
    }

    override fun onDialogDismissed(dialogId: Int) {

    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        updateThumbnailColor(color, taskFragViewModel.defaultTaskColor)
    }

    private fun updateThumbnailColor(color: Int, defColor: Int) {
        if (color == 0) {
            (thumbnail.drawable.mutate() as ColorDrawable).color = defColor
        } else {
            (thumbnail.drawable.mutate() as ColorDrawable).color = color
        }
    }

    override fun onTaskDescPicked(taskDescription: TaskDescription) {
        taskText.setText(taskDescription.desc)
        updateThumbnailColor(taskDescription.colorInt, taskFragViewModel.defaultTaskColor)
    }

    private fun hideKeyboardIfShown() {
        if (inputMethodManager.isActive) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_THUMBNAIL_COLOR, currentThumbnailColor)
    }
}