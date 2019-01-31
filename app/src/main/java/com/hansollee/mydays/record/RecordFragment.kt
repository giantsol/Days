package com.hansollee.mydays.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hansollee.mydays.R

/**
 * Created by kevin-ee on 2019-01-31.
 */

class RecordFragment: Fragment() {

    private lateinit var floatingButton: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        floatingButton = view.findViewById(R.id.floating_button)
        floatingButton.setOnClickListener { _ ->
            Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show()
        }
    }
}