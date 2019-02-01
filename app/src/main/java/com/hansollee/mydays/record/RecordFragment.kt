package com.hansollee.mydays.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hansollee.mydays.R
import com.hansollee.mydays.toDisplayFormat
import java.util.Date

/**
 * Created by kevin-ee on 2019-01-31.
 */

class RecordFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel = ViewModelProviders.of(activity).get(RecordFragmentViewModel::class.java)

        val floatingButton: FloatingActionButton = view.findViewById(R.id.floating_button)
        floatingButton.setOnClickListener { _ ->
            showRecordEditorDialog()
        }

        val arrowBack: View = view.findViewById(R.id.arrow_back)
        arrowBack.setOnClickListener { _ ->
            viewModel.changeCurrentDate(-1)
        }

        val arrowForward: View = view.findViewById(R.id.arrow_forward)
        arrowForward.setOnClickListener { _ ->
            viewModel.changeCurrentDate(1)
        }

        val dateText: TextView = view.findViewById(R.id.date_text)
        dateText.setOnClickListener { _ ->
            viewModel.resetCurrentDateToToday()
        }

        viewModel.getCurrentDateLiveData().observe(this, Observer<Date> { currentDate ->
            dateText.text = currentDate.toDisplayFormat()
        })
    }

    private fun showRecordEditorDialog() {
        val transaction = fragmentManager.beginTransaction()
        val dialog = RecordEditorDialog.newInstance()
        dialog.show(transaction, null)
    }
}