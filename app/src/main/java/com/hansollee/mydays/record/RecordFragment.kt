package com.hansollee.mydays.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hansollee.mydays.R
import com.hansollee.mydays.models.Record
import com.hansollee.mydays.toStringFormat
import com.hansollee.mydays.toast
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-01-31.
 */

class RecordFragment: Fragment(), RecordListAdapter.RecordItemClickListener {

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

        val recordList: RecyclerView = view.findViewById(R.id.record_list)
        recordList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val recordListAdapter = RecordListAdapter(context, viewModel, this)
        recordList.adapter = recordListAdapter

        viewModel.getCurrentDateLiveData().observe(this, Observer<LocalDate> { currentDate ->
            dateText.text = currentDate.toStringFormat()
            viewModel.loadRecordsForDate(currentDate)
        })

        viewModel.getRecordsLiveData().observe(this, Observer<List<Record>> { records ->
            recordListAdapter.setRecords(records)
        })
    }

    private fun showRecordEditorDialog(record: Record? = null) {
        val transaction = fragmentManager.beginTransaction()
        val dialog = RecordEditorDialog.newInstance(record)
        dialog.show(transaction, null)
    }

    override fun onItemClick(record: Record) {
        showRecordEditorDialog(record)
    }
}