package com.hansollee.mydays.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hansollee.mydays.GlobalViewModel
import com.hansollee.mydays.MainActivity
import com.hansollee.mydays.R
import com.hansollee.mydays.models.History
import com.hansollee.mydays.tasks.TasksViewModel
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-01-31.
 */

class HistoryFragment : Fragment(), HistoryListAdapter.ItemClickListener {

    private lateinit var tasksViewModel: TasksViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val globalViewModel = GlobalViewModel.getInstance(activity)
        val historyViewModel = HistoryViewModel.getInstance(activity, globalViewModel.getTodayValue())
        tasksViewModel = TasksViewModel.getInstance(activity, globalViewModel.getTodayValue())
        val historyListView: RecyclerView = view.findViewById(R.id.history_list)
        val historyListAdapter = HistoryListAdapter(context, historyViewModel, globalViewModel, this)
        val emptyView: View = view.findViewById(R.id.empty_view)

        historyListView.layoutManager = LinearLayoutManager(context)
        historyListView.adapter = historyListAdapter

        historyViewModel.getAllHistory().observe(this, Observer<List<History>> { items ->
            if (items.isEmpty()) {
                emptyView.visibility = View.VISIBLE
                historyListView.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                historyListView.visibility = View.VISIBLE
            }

            historyListAdapter.updateItems(items)
        })

        historyViewModel.getHasMoreItems().observe(this, Observer<Boolean> { hasMoreItems ->
            historyListAdapter.setShowFooter(hasMoreItems)
        })

        globalViewModel.getToday().observe(this, Observer<LocalDate> { today ->
            if (historyViewModel.updateTodayValue(today)) {
                historyViewModel.reloadHistory()
            }
        })
    }

    override fun onItemClick(history: History) {
        (activity as MainActivity).goToPage(0, true, { tasksViewModel.setCurrentDate(history.date) })
    }
}
