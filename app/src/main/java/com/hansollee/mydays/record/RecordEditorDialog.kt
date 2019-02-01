package com.hansollee.mydays.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hansollee.mydays.R
import com.hansollee.mydays.models.Record
import com.hansollee.mydays.toDisplayFormat
import com.hansollee.mydays.widgets.CustomTimePicker
import java.util.Date

/**
 * Created by kevin-ee on 2019-02-01.
 */

class RecordEditorDialog : DialogFragment() {

    private data class ValidityCheckResult(val isOk: Boolean, val errorMessage: String?)

    companion object {
        private const val KEY_RECORD = "key.record"

        fun newInstance(record: Record? = null): RecordEditorDialog {
            val instance = RecordEditorDialog()

//            val args = Bundle()
//            args.putParcelable(KEY_RECORD, record)
//            instance.arguments = args

            return instance
        }
    }

    private lateinit var fromTimePicker: CustomTimePicker
    private lateinit var toTimePicker: CustomTimePicker
    private lateinit var taskText: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val recordFragViewModel = ViewModelProviders.of(activity).get(RecordFragmentViewModel::class.java)
        val view = inflater.inflate(R.layout.dialog_create_record, container, false)

        val currentDate: TextView = view.findViewById(R.id.current_date)

        fromTimePicker = view.findViewById(R.id.from_timepicker)
        toTimePicker = view.findViewById(R.id.to_timepicker)
        taskText = view.findViewById(R.id.task_input)

        val cancelButton: Button = view.findViewById(R.id.cancel_button)
        cancelButton.setOnClickListener { _ ->
            dismiss()
        }

        val okButton: Button = view.findViewById(R.id.ok_button)
        okButton.setOnClickListener { _ ->
            // 우선 input들이 모두 valid한지 체크
            val validityCheckResult = getValidityCheckResult()
            if (validityCheckResult.isOk) {
                dismiss()
            } else {
                toast(validityCheckResult.errorMessage)
            }
        }

        recordFragViewModel.getCurrentDateLiveData().observe(this, Observer<Date> { date ->
            currentDate.text = date.toDisplayFormat()
        })

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog.also {
            it.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setCanceledOnTouchOutside(false)
        }
    }

    private fun getValidityCheckResult(): ValidityCheckResult {
        val fromTime: CustomTimePicker.Time = fromTimePicker.getTime()
        val toTime: CustomTimePicker.Time = toTimePicker.getTime()
        val timeDifference = toTime.minus(fromTime)
        if (timeDifference <= 0) {
            return ValidityCheckResult(false, "to는 from보다 늦은 시간이어야 합니다")
        }

        if (taskText.text.trim().isEmpty()) {
            return ValidityCheckResult(false, "Task에 글자를 적어주세요!")
        }

        return ValidityCheckResult(true, null)
    }

    private fun toast(msg: String?) {
        Toast.makeText(context, msg ?: "No Error Message", Toast.LENGTH_SHORT).show()
    }
}