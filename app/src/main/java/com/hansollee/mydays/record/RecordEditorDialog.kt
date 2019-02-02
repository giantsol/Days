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
import com.hansollee.mydays.widgets.SimpleTimePicker
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
    private lateinit var currentDateText: TextView
    private lateinit var fromTimePicker: SimpleTimePicker
    private lateinit var toTimePicker: SimpleTimePicker
    private lateinit var taskText: EditText

    // record를 클릭해서 열렸으면 Nonnull, 새로만들기 버튼을 클릭해서 열렸으면 null
    private var originalRecord: Record? = null

    private lateinit var fromToInvalidMsg: String
    private lateinit var taskDescriptionInvalidMsg: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        originalRecord = arguments.getParcelable(KEY_RECORD)

        val view = inflater.inflate(R.layout.dialog_create_record, container, false)
        recordFragViewModel = ViewModelProviders.of(activity).get(RecordFragmentViewModel::class.java)
        currentDateText = view.findViewById(R.id.current_date)
        fromTimePicker = view.findViewById(R.id.from_timepicker)
        toTimePicker = view.findViewById(R.id.to_timepicker)
        taskText = view.findViewById(R.id.task_input)
        val cancelButton: Button = view.findViewById(R.id.cancel_button)
        val deleteButton: Button = view.findViewById(R.id.delete_button)
        val okButton: Button = view.findViewById(R.id.ok_button)

        fromToInvalidMsg = context.resources.getString(R.string.from_and_to_invalid)
        taskDescriptionInvalidMsg = context.resources.getString(R.string.task_description_invalid)

        cancelButton.setOnClickListener { _ ->
            dismiss()
        }

        if (originalRecord != null) {
            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener { _ ->
                recordFragViewModel.deleteRecord(originalRecord!!)
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

        recordFragViewModel.getCurrentDate().observe(this, Observer<LocalDate> { date ->
            currentDateText.text = date.toStringFormat()
        })

        if (originalRecord != null) {
            fillViewsWithRecord(originalRecord!!)
        }

        return view
    }

    private fun getValidityCheckResult(): ValidityCheckResult {
        val fromTime = fromTimePicker.time
        val toTime = toTimePicker.time
        if (fromTime >= toTime) {
            return ValidityCheckResult(false, fromToInvalidMsg)
        }

        if (taskText.text.trim().isEmpty()) {
            return ValidityCheckResult(false, taskDescriptionInvalidMsg)
        }

        return ValidityCheckResult(true, null)
    }

    private fun createNewRecordFromInputs(): Record {
        val date = originalRecord?.date ?: recordFragViewModel.getCurrentDate().value
        return Record(date, fromTimePicker.time, toTimePicker.time, taskText.text.toString())
    }

    private fun getUpdatedRecordWithInputs(originalRecord: Record): Record
        = createNewRecordFromInputs().also { it.id = originalRecord.id }

    private fun fillViewsWithRecord(record: Record) {
        val fromTime = record.fromTime
        val toTime = record.toTime

        currentDateText.text = record.date.toStringFormat()

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