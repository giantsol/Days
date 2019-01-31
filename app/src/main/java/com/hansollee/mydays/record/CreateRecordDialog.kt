package com.hansollee.mydays.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
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
        val view = inflater.inflate(R.layout.dialog_create_record, container, false)
        return view
    }
}