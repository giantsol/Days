package com.hansollee.mydays.tasks

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hansollee.mydays.GlobalViewModel
import com.hansollee.mydays.R
import com.hansollee.mydays.history.HistoryViewModel
import com.hansollee.mydays.models.Task
import com.hansollee.mydays.toDisplayFormat
import com.hansollee.mydays.toLocalDate
import io.reactivex.disposables.Disposable
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-01-31.
 */

class TasksFragment: Fragment(), TaskListAdapter.ItemClickListener {

    private var observeDateUpdatedByUserWork: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val globalViewModel = GlobalViewModel.getInstance(activity)
        val tasksViewModel = TasksViewModel.getInstance(activity, globalViewModel.getTodayValue())
        val historyViewModel = HistoryViewModel.getInstance(activity, globalViewModel.getTodayValue())
        val floatingButton: FloatingActionButton = view.findViewById(R.id.floating_button)
        val arrowBack: View = view.findViewById(R.id.arrow_back)
        val arrowForward: View = view.findViewById(R.id.arrow_forward)
        val dateText: TextView = view.findViewById(R.id.date_text)
        val taskList: RecyclerView = view.findViewById(R.id.task_list)
        val taskListAdapter = TaskListAdapter(context, this, tasksViewModel)
        val emptyView: View = view.findViewById(R.id.empty_view)

        floatingButton.setOnClickListener { _ ->
            showTaskEditorDialog()
        }

        arrowBack.setOnClickListener { _ ->
            tasksViewModel.changeCurrentDate(-1)
        }

        arrowForward.setOnClickListener { _ ->
            tasksViewModel.changeCurrentDate(1)
        }

        dateText.setOnClickListener { v ->
            val date = (v as TextView).text.toString().toLocalDate()
            val datePickerDialog = DatePickerDialog(context,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    tasksViewModel.setCurrentDate(LocalDate.of(year, month + 1, dayOfMonth))
                },
                date.year, date.monthValue - 1, date.dayOfMonth)
            datePickerDialog.show()
        }

        taskList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = taskListAdapter
            itemAnimator = object: DefaultItemAnimator() {

                override fun animateChange(oldHolder: RecyclerView.ViewHolder?, newHolder: RecyclerView.ViewHolder?, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
                    if (oldHolder == newHolder) {
                        dispatchChangeFinished(oldHolder, true)
                    } else {
                        dispatchChangeFinished(oldHolder, true)
                        dispatchChangeFinished(newHolder, false)
                    }

                    return false
                }

                override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
                    dispatchAddFinished(holder)
                    return false
                }

                override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
                    dispatchRemoveFinished(holder)
                    return false
                }
            }
        }

        globalViewModel.getToday().observe(this, Observer<LocalDate> { today ->
            if (tasksViewModel.updateTodayValue(today)) {
                dateText.text = tasksViewModel.getCurrentDateValue().toDisplayFormat(today)
            }
        })

        tasksViewModel.also {
            it.getCurrentDate().observe(this, Observer<LocalDate> { currentDate ->
                dateText.text = currentDate.toDisplayFormat(globalViewModel.getTodayValue())
                tasksViewModel.loadTasksForDate(listOf(currentDate))
            })

            it.getCurrentTasks().observe(this, Observer<List<Task>> { tasks ->
                if (tasks.isEmpty()) {
                    emptyView.visibility = View.VISIBLE
                    taskList.visibility = View.GONE
                } else {
                    emptyView.visibility = View.GONE
                    taskList.visibility = View.VISIBLE
                }

                taskListAdapter.updateItems(tasks)

            })

            observeDateUpdatedByUserWork = it.observeDateUpdatedByUser().subscribe { pair ->
                historyViewModel.onUserUpdatedTasks(pair.first, pair.second)
            }

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

    override fun onDestroyView() {
        super.onDestroyView()

        observeDateUpdatedByUserWork?.dispose()
    }
}