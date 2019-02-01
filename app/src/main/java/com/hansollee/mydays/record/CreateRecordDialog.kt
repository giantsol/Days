package com.hansollee.mydays.record

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.hansollee.mydays.R

/**
 * Created by kevin-ee on 2019-02-01.
 */

class CreateRecordDialog : DialogFragment() {

    companion object {
        fun newInstance(): CreateRecordDialog {
            val instance = CreateRecordDialog()

            val args = Bundle()
            args.putString("key", "value")
            instance.arguments = args

            return instance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // prevent auto cancel
        isCancelable = false

        val recordFragViewModel = ViewModelProviders.of(activity).get(RecordFragmentViewModel::class.java)

        val view = inflater.inflate(R.layout.dialog_create_record, container, false)

        val cancelButton: Button = view.findViewById(R.id.cancel_button)
        cancelButton.setOnClickListener { _ ->
            dismiss()
        }

        val okButton: Button = view.findViewById(R.id.ok_button)
        okButton.setOnClickListener { _ ->
            //TODO: notify new record added to viewmodel
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}