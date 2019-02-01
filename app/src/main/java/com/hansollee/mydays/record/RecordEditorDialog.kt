package com.hansollee.mydays.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.hansollee.mydays.R
import com.hansollee.mydays.models.Record

/**
 * Created by kevin-ee on 2019-02-01.
 */

class RecordEditorDialog : DialogFragment() {

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
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
        dialog.also {
            it.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setCanceledOnTouchOutside(false)
        }

    }

}