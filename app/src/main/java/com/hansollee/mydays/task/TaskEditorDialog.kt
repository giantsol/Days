package com.hansollee.mydays.task

import android.app.Activity
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
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
import org.threeten.bp.LocalTime

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
    private lateinit var startTimePicker: SimpleTimePicker
    private lateinit var endTimePicker: SimpleTimePicker
    private lateinit var taskText: EditText
    private lateinit var thumbnail: ImageView

    // task를 클릭해서 열렸으면 nonnull, 새로만들기 버튼을 클릭해서 열렸으면 null
    private var originalTask: Task? = null

    private lateinit var fromToInvalidMsg: String
    private lateinit var taskDescriptionInvalidMsg: String

    private val currentThumbnailColor: Int
        get() = (thumbnail.drawable as ColorDrawable).color

    private lateinit var inputMethodManager: InputMethodManager

    private lateinit var endTimeCheckbox: CheckBox
    private lateinit var proceedingText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        originalTask = arguments.getParcelable(KEY_TASK)

        val view = inflater.inflate(R.layout.dialog_task_editor, container, false)
        taskFragViewModel = ViewModelProviders.of(activity).get(TaskFragmentViewModel::class.java)
        val title: TextView = view.findViewById(R.id.title)
        val endTimeTextContainer: View = view.findViewById(R.id.end_time_text_container)
        endTimeCheckbox = view.findViewById(R.id.end_time_checkbox)
        proceedingText = view.findViewById(R.id.text_proceeding)
        startTimePicker = view.findViewById(R.id.start_timepicker)
        endTimePicker = view.findViewById(R.id.end_timepicker)
        taskText = view.findViewById(R.id.task_input)
        thumbnail = view.findViewById(R.id.thumbnail)
        val previousTasksButton: View = view.findViewById(R.id.previous_tasks_button)
        val cancelButton: Button = view.findViewById(R.id.cancel_button)
        val deleteButton: Button = view.findViewById(R.id.delete_button)
        val okButton: Button = view.findViewById(R.id.ok_button)

        inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        val res = context.resources
        fromToInvalidMsg = res.getString(R.string.from_and_to_invalid)
        taskDescriptionInvalidMsg = res.getString(R.string.task_description_invalid)
        val createNewTaskTitle = res.getString(R.string.create_new_task_title)
        val editTaskTitle = res.getString(R.string.edit_task_title)

        title.text = if (originalTask == null) createNewTaskTitle else editTaskTitle

        endTimeTextContainer.setOnClickListener { _ ->
            endTimeCheckbox.isChecked = !endTimeCheckbox.isChecked
        }

        endTimeCheckbox.setOnCheckedChangeListener { v, isChecked ->
            toggleEndTimePickerVisibility(isChecked)

            if (isChecked) {
                endTimePicker.setTime(LocalTime.now())
            }
        }

        startTimePicker.setOnTimeChangedListener(object: SimpleTimePicker.OnTimeChangedListener {
            override fun onTimeChanged(hourOfDay: Int, minute: Int) {
                if (endTimePicker.visibility == View.VISIBLE && endTimePicker < startTimePicker) {
                    endTimePicker.setTime(hourOfDay, minute)
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

        previousTasksButton.setOnClickListener { _ ->
            hideKeyboardIfShown()

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
            startTimePicker.setTime(LocalTime.now())
        }

        if (savedInstanceState != null) {
            updateThumbnailColor(savedInstanceState.getInt(KEY_THUMBNAIL_COLOR))
        }

        return view
    }

    private fun toggleEndTimePickerVisibility(pickerVisible: Boolean) {
        if (pickerVisible) {
            endTimePicker.visibility = View.VISIBLE
            proceedingText.visibility = View.GONE
        } else {
            endTimePicker.visibility = View.GONE
            proceedingText.visibility = View.VISIBLE
        }
    }

    private fun getValidityCheckResult(): ValidityCheckResult {
        val startTime = startTimePicker.time
        val endTime = if (endTimePicker.visibility == View.VISIBLE) endTimePicker.time else null
        if (endTime != null && startTime > endTime) {
            return ValidityCheckResult(false, fromToInvalidMsg)
        }

        if (taskText.text.trim().isEmpty()) {
            return ValidityCheckResult(false, taskDescriptionInvalidMsg)
        }

        return ValidityCheckResult(true, null)
    }

    private fun createNewTaskFromInputs(): Task {
        val date = originalTask?.date ?: taskFragViewModel.getCurrentDate().value
        return Task(
            date,
            startTimePicker.time,
            if (endTimePicker.visibility == View.VISIBLE) endTimePicker.time else null,
            taskText.text.toString(),
            (thumbnail.drawable as ColorDrawable).color)
    }

    private fun getUpdatedTaskWithInputs(originalTask: Task): Task
        = createNewTaskFromInputs().also { it.id = originalTask.id }

    private fun fillViewsWithTask(task: Task) {
        val startTime = task.startTime
        val endTime = task.endTime

        startTimePicker.setTime(startTime)

        endTimeCheckbox.isChecked = endTime != null
        if (endTime != null) {
            endTimePicker.setTime(endTime)
        }

        taskText.setText(task.desc)
        updateThumbnailColor(task.colorInt)
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
        updateThumbnailColor(color)
    }

    private fun updateThumbnailColor(color: Int) {
        (thumbnail.drawable.mutate() as ColorDrawable).color = color
    }

    override fun onTaskDescPicked(taskDescription: TaskDescription) {
        taskText.setText(taskDescription.desc)
        updateThumbnailColor(taskDescription.colorInt)
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

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
    }
}