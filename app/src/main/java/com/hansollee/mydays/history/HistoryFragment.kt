package com.hansollee.mydays.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hansollee.mydays.R
import com.hansollee.mydays.models.History

/**
 * Created by kevin-ee on 2019-01-31.
 */

class HistoryFragment : Fragment(), HistoryListAdapter.HistoryItemClickListener {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel = ViewModelProviders.of(activity).get(HistoryFragmentViewModel::class.java)
        val historyList: RecyclerView = view.findViewById(R.id.history_list)
        val historyListAdapter = HistoryListAdapter(context, viewModel, this)

        historyList.layoutManager = LinearLayoutManager(context)
        historyList.adapter = historyListAdapter

        viewModel.getAllHistoryItems().observe(this, Observer<List<History>> { items ->
            historyListAdapter.updateHistoryItems(items)
        })
    }

    override fun onItemClick(history: History) {

    }
}
