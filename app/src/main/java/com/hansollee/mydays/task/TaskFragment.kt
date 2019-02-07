package com.hansollee.mydays.task

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
import com.hansollee.mydays.history.HistoryFragmentViewModel
import com.hansollee.mydays.models.Task
import com.hansollee.mydays.toStringFormat
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-01-31.
 */

class TaskFragment: Fragment(), TaskListAdapter.ItemClickListener {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel = ViewModelProviders.of(activity).get(TaskFragmentViewModel::class.java)
        val historyFragviewModel = ViewModelProviders.of(activity).get(HistoryFragmentViewModel::class.java)
        val floatingButton: FloatingActionButton = view.findViewById(R.id.floating_button)
        val arrowBack: View = view.findViewById(R.id.arrow_back)
        val arrowForward: View = view.findViewById(R.id.arrow_forward)
        val dateText: TextView = view.findViewById(R.id.date_text)
        val taskList: RecyclerView = view.findViewById(R.id.task_list)
        val taskListAdapter = TaskListAdapter(context, viewModel, this)
        val progressView: View = view.findViewById(R.id.progress_bar)
        val emptyView: View = view.findViewById(R.id.empty_view)

        floatingButton.setOnClickListener { _ ->
            showTaskEditorDialog()
        }

        arrowBack.setOnClickListener { _ ->
            viewModel.changeCurrentDate(-1)
        }

        arrowForward.setOnClickListener { _ ->
            viewModel.changeCurrentDate(1)
        }

        dateText.setOnClickListener { _ ->
            viewModel.resetCurrentDateToToday()
        }

        taskList.also {
            it.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            it.adapter = taskListAdapter
        }

        viewModel.also {
            it.getCurrentDate().observe(this, Observer<LocalDate> { currentDate ->
                dateText.text = currentDate.toStringFormat()
                viewModel.loadTasksForDate(currentDate)
            })

            it.getCurrentTasks().observe(this, Observer<TaskFragmentViewModel.TasksResult> { tasksResult ->
                val tasks = tasksResult.tasks

                if (tasks.isEmpty()) {
                    emptyView.visibility = View.VISIBLE
                    taskList.visibility = View.GONE
                } else {
                    emptyView.visibility = View.GONE
                    taskList.visibility = View.VISIBLE
                }

                taskListAdapter.setTasks(tasks)

                if (tasksResult.byUpdate) {
                    historyFragviewModel.onTasksUpdated(it.getCurrentDate().value, tasks)
                }

            })

            it.getLoadingStatus().observe(this, Observer<Boolean> { isLoading ->
                if (isLoading) {
                    progressView.visibility = View.VISIBLE
                } else {
                    progressView.visibility = View.GONE
                }
            })
        }
    }

    private fun showTaskEditorDialog(task: Task? = null) {
        val transaction = fragmentManager.beginTransaction()
        val dialog = TaskEditorDialog.newInstance(task)
        dialog.show(transaction, null)
    }

    override fun onItemClick(task: Task) {
        showTaskEditorDialog(task)
    }
}