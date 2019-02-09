package com.hansollee.mydays.tasks

import android.app.Activity
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
import com.hansollee.mydays.GlobalViewModel
import com.hansollee.mydays.R
import com.hansollee.mydays.models.Task
import com.hansollee.mydays.models.TaskPickerItem
import com.hansollee.mydays.toDisplayFormat
import com.hansollee.mydays.toLocalDate
import com.hansollee.mydays.toast
import com.hansollee.mydays.widgets.SimpleTimePicker
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

/**
 * Created by kevin-ee on 2019-02-01.
 */

class TaskEditorDialog : DialogFragment(), ColorPickerDialogListener, TaskPickerDialog.Listener {

    private data class ValidityCheckResult(val isOk: Boolean, val errorMessage: String?)

    companion object {
        private const val KEY_TASK = "key.task"
        private const val KEY_THUMBNAIL_COLOR = "key.thumbnail.color"

        private const val TAG_COLOR_PICKER = "ColorPicker"
        private const val TAG_TASK_PICKER = "TaskPicker"

        fun newInstance(task: Task? = null): TaskEditorDialog {
            val instance = TaskEditorDialog()

            val args = Bundle()
            args.putParcelable(KEY_TASK, task)
            instance.arguments = args

            return instance
        }
    }

    private lateinit var globalViewModel: GlobalViewModel
    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var startTimePicker: SimpleTimePicker
    private lateinit var endTimePicker: SimpleTimePicker
    private lateinit var taskDescriptionView: EditText
    private lateinit var thumbnail: ImageView
    private lateinit var startDateText: TextView
    private lateinit var endDateText: TextView
    private lateinit var endTimeContainer: View

    // task를 클릭해서 열렸으면 nonnull, 새로만들기 버튼을 클릭해서 열렸으면 null
    private var originalTask: Task? = null

    private lateinit var startEndInvalidMsg: String
    private lateinit var taskDescriptionInvalidMsg: String

    private val currentThumbnailColor: Int
        get() = (thumbnail.drawable as ColorDrawable).color

    private lateinit var inputMethodManager: InputMethodManager

    private lateinit var endTimeCheckbox: CheckBox
    private lateinit var proceedingText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.dialog_task_editor, container, false)
        originalTask = arguments.getParcelable(KEY_TASK)
        globalViewModel = GlobalViewModel.getInstance(activity)
        tasksViewModel = TasksViewModel.getInstance(activity, globalViewModel.getTodayValue())
        val titleView: TextView = view.findViewById(R.id.title)
        val endTimeTextContainer: View = view.findViewById(R.id.end_time_text_container)
        endTimeCheckbox = view.findViewById(R.id.end_time_checkbox)
        proceedingText = view.findViewById(R.id.text_proceeding)
        startTimePicker = view.findViewById(R.id.start_timepicker)
        endTimePicker = view.findViewById(R.id.end_timepicker)
        taskDescriptionView = view.findViewById(R.id.task_description)
        thumbnail = view.findViewById(R.id.thumbnail)
        startDateText = view.findViewById(R.id.start_date_text)
        endDateText = view.findViewById(R.id.end_date_text)
        endTimeContainer = view.findViewById(R.id.end_time_container)
        val previousTasksButton: View = view.findViewById(R.id.previous_tasks_button)
        val cancelButton: Button = view.findViewById(R.id.cancel_button)
        val deleteButton: Button = view.findViewById(R.id.delete_button)
        val okButton: Button = view.findViewById(R.id.ok_button)
        inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val res = context.resources

        startEndInvalidMsg = res.getString(R.string.start_end_time_invalid)
        taskDescriptionInvalidMsg = res.getString(R.string.task_description_invalid)
        val createNewTaskTitle = res.getString(R.string.create_new_task_title)
        val editTaskTitle = res.getString(R.string.edit_task_title)

        titleView.text = if (originalTask == null) createNewTaskTitle else editTaskTitle

        endTimeTextContainer.setOnClickListener { _ ->
            endTimeCheckbox.isChecked = !endTimeCheckbox.isChecked
        }

        endTimeCheckbox.setOnCheckedChangeListener { v, isChecked ->
            toggleEndTimeVisibility(isChecked)

            // 마침시간 체크 할때마다 현재 시각으로 설정되도록
            if (isChecked) {
                endDateText.text = globalViewModel.getTodayValue().toDisplayFormat(globalViewModel.getTodayValue())
                endTimePicker.setTime(LocalTime.now())
            }
        }

        startTimePicker.setOnTimeChangedListener(object: SimpleTimePicker.OnTimeChangedListener {
            override fun onTimeChanged(hourOfDay: Int, minute: Int) {
                // TODO: 이 기능은 없애고 다음 날 까지 이어지도록 해야함
                if (endTimeContainer.visibility == View.VISIBLE && endTimePicker < startTimePicker) {
                    endTimePicker.setTime(hourOfDay, minute)
                }
            }
        })

        // 죽었다 살아났을 때 기존에 떠있던 dialog에 콜백 다시 달아줌
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

        // 죽었다 살아났을 때 기존에 떠있던 dialog에 콜백 다시 달아줌
        (fragmentManager.findFragmentByTag(TAG_TASK_PICKER) as? TaskPickerDialog)
            ?.setListener(this)

        previousTasksButton.setOnClickListener { _ ->
            hideKeyboardIfShown()

            TaskPickerDialog()
                .apply { setListener(this@TaskEditorDialog) }
                .show(fragmentManager.beginTransaction(), TAG_TASK_PICKER)
        }

        cancelButton.setOnClickListener { _ ->
            dismiss()
        }

        if (originalTask != null) {
            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener { _ ->
                tasksViewModel.deleteTask(originalTask!!)
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
                    tasksViewModel.insertNewTask(createNewTaskFromInputs())
                } else {
                    tasksViewModel.updateTask(getUpdatedTaskWithInputs(originalTask!!))
                }
                dismiss()
            } else {
                toast(validityCheckResult.errorMessage)
            }
        }

        if (originalTask != null) {
            updateViewsWithTask(originalTask!!)
        } else {
            startDateText.text = tasksViewModel.getCurrentDateValue().toDisplayFormat(globalViewModel.getTodayValue())
            startTimePicker.setTime(LocalTime.now())
        }

        if (savedInstanceState != null) {
            updateThumbnailColor(savedInstanceState.getInt(KEY_THUMBNAIL_COLOR))
        }

        return view
    }

    private fun toggleEndTimeVisibility(endTimeVisible: Boolean) {
        if (endTimeVisible) {
            endTimeContainer.visibility = View.VISIBLE
            proceedingText.visibility = View.INVISIBLE
        } else {
            endTimeContainer.visibility = View.INVISIBLE
            proceedingText.visibility = View.VISIBLE
        }
    }

    private fun getValidityCheckResult(): ValidityCheckResult {
        val start = getStartTime()
        val end = getEndTime()
        if (end != null && start > end) {
            return ValidityCheckResult(false, startEndInvalidMsg)
        }

        if (taskDescriptionView.text.trim().isEmpty()) {
            return ValidityCheckResult(false, taskDescriptionInvalidMsg)
        }

        return ValidityCheckResult(true, null)
    }

    private fun createNewTaskFromInputs(): Task {
        val start = getStartTime()
        val end = getEndTime()
        return Task(
            start,
            end,
            taskDescriptionView.text.toString(),
            (thumbnail.drawable as ColorDrawable).color)
    }

    private fun getStartTime(): LocalDateTime
        = LocalDateTime.of(startDateText.text.toString().toLocalDate(), startTimePicker.time)

    // 마침시간 체크 여부에 따라 null을 주거나 endTimePicker의 값을 줌
    private fun getEndTime(): LocalDateTime?
        = if (endTimeContainer.visibility == View.VISIBLE)
        LocalDateTime.of(endDateText.text.toString().toLocalDate(), endTimePicker.time) else null

    private fun getUpdatedTaskWithInputs(originalTask: Task): Task
        = createNewTaskFromInputs().also { it.id = originalTask.id }

    private fun updateViewsWithTask(task: Task) {
        val start = task.startDateTime
        val end = task.endDateTime

        startDateText.text = start.toLocalDate().toDisplayFormat(globalViewModel.getTodayValue())
        startTimePicker.setTime(start.toLocalTime())

        endTimeCheckbox.isChecked = end != null
        if (end != null) {
            endDateText.text = end.toLocalDate().toDisplayFormat(globalViewModel.getTodayValue())
            endTimePicker.setTime(end.toLocalTime())
        }

        taskDescriptionView.setText(task.desc)
        updateThumbnailColor(task.colorInt)
    }

    override fun onStart() {
        super.onStart()
        dialog.also {
            it.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setCanceledOnTouchOutside(false)
        }
    }

    // ColorPickerListener꺼... 왜 이렇게 만들었징
    override fun onDialogDismissed(dialogId: Int) { }

    override fun onColorSelected(dialogId: Int, color: Int) {
        updateThumbnailColor(color)
    }

    private fun updateThumbnailColor(color: Int) {
        (thumbnail.drawable.mutate() as ColorDrawable).color = color
    }

    override fun onTaskPicked(taskPickerItem: TaskPickerItem) {
        taskDescriptionView.setText(taskPickerItem.desc)
        updateThumbnailColor(taskPickerItem.colorInt)
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