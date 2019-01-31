package com.hansollee.mydays.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hansollee.mydays.R
import com.hansollee.mydays.toDisplayFormat
import java.util.*

/**
 * Created by kevin-ee on 2019-01-31.
 */

class RecordFragment: Fragment() {

    private lateinit var floatingButton: FloatingActionButton
    private lateinit var arrowBack: View
    private lateinit var arrowForward: View
    private lateinit var dateText: TextView

    private lateinit var viewModel: RecordFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(RecordFragmentViewModel::class.java)

        floatingButton = view.findViewById(R.id.floating_button)
        floatingButton.setOnClickListener { _ ->
            Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show()
        }

        arrowBack = view.findViewById(R.id.arrow_back)
        arrowBack.setOnClickListener { _ ->
            viewModel.moveOneDayBefore()
        }
        arrowForward = view.findViewById(R.id.arrow_forward)
        arrowForward.setOnClickListener { _ ->
            viewModel.moveOneDayAfter()
        }

        dateText = view.findViewById(R.id.date_text)
        viewModel.getCurrentDateLiveData().observe(this, Observer<Date> { currentDate ->
            dateText.text = currentDate.toDisplayFormat()
        })
        dateText.setOnClickListener { _ ->
            viewModel.resetCurrentDateToToday()
        }
    }
}