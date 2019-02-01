package com.hansollee.mydays.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hansollee.mydays.R
import com.hansollee.mydays.models.Record
import com.hansollee.mydays.toStringFormat
import com.hansollee.mydays.toast
import com.hansollee.mydays.widgets.CustomTimePicker
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-02-01.
 */

class RecordEditorDialog : DialogFragment() {

    private data class ValidityCheckResult(val isOk: Boolean, val errorMessage: String?)

    companion object {
        private const val KEY_RECORD = "key.record"

        fun newInstance(record: Record? = null): RecordEditorDialog {
            val instance = RecordEditorDialog()

            val args = Bundle()
            args.putParcelable(KEY_RECORD, record)
            instance.arguments = args

            return instance
        }
    }

    private lateinit var recordFragViewModel: RecordFragmentViewModel
    private lateinit var fromTimePicker: CustomTimePicker
    private lateinit var toTimePicker: CustomTimePicker
    private lateinit var taskText: EditText

    private var originalRecord: Record? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        originalRecord = arguments.getParcelable(KEY_RECORD)

        recordFragViewModel = ViewModelProviders.of(activity).get(RecordFragmentViewModel::class.java)
        val view = inflater.inflate(R.layout.dialog_create_record, container, false)

        val currentDate: TextView = view.findViewById(R.id.current_date)

        fromTimePicker = view.findViewById(R.id.from_timepicker)
        toTimePicker = view.findViewById(R.id.to_timepicker)
        taskText = view.findViewById(R.id.task_input)

        val cancelButton: Button = view.findViewById(R.id.cancel_button)
        cancelButton.setOnClickListener { _ ->
            dismiss()
        }

        val deleteButton: Button = view.findViewById(R.id.delete_button)
        if (originalRecord != null) {
            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener { _ ->
                recordFragViewModel.deleteRecord(originalRecord!!)
                dismiss()
            }
        } else {
            deleteButton.visibility = View.GONE
        }

        val okButton: Button = view.findViewById(R.id.ok_button)
        okButton.setOnClickListener { _ ->
            // 우선 input들이 모두 valid한지 체크
            val validityCheckResult = getValidityCheckResult()
            if (validityCheckResult.isOk) {
                if (originalRecord == null) {
                    recordFragViewModel.insertNewRecord(createNewRecordFromInputs())
                } else {
                    recordFragViewModel.updateRecord(getUpdatedRecordWithInputs(originalRecord!!))
                }
                dismiss()
            } else {
                toast(validityCheckResult.errorMessage)
            }
        }

        recordFragViewModel.getCurrentDateLiveData().observe(this, Observer<LocalDate> { date ->
            currentDate.text = date.toStringFormat()
        })

        if (originalRecord != null) {
            fillViewsWithRecord(originalRecord!!)
        }

        return view
    }

    private fun getValidityCheckResult(): ValidityCheckResult {
        val fromTime = fromTimePicker.time
        val toTime = toTimePicker.time
        if (fromTime > toTime) {
            return ValidityCheckResult(false, "to는 from보다 늦은 시간이어야 합니다")
        }

        if (taskText.text.trim().isEmpty()) {
            return ValidityCheckResult(false, "Task에 글자를 적어주세요!")
        }

        return ValidityCheckResult(true, null)
    }

    private fun createNewRecordFromInputs(): Record
        = Record(recordFragViewModel.getCurrentDateLiveData().value,
        fromTimePicker.time, toTimePicker.time, taskText.text.toString())

    private fun getUpdatedRecordWithInputs(originalRecord: Record): Record
        = createNewRecordFromInputs().also { it.id = originalRecord.id }

    private fun fillViewsWithRecord(record: Record) {
        val fromTime = record.fromTime
        val toTime = record.toTime

        fromTimePicker.showTime(fromTime)
        toTimePicker.showTime(toTime)

        taskText.setText(record.task)
    }

    override fun onStart() {
        super.onStart()
        dialog.also {
            it.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setCanceledOnTouchOutside(false)
        }
    }
}