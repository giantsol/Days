package com.hansollee.mydays.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hansollee.mydays.GlobalViewModel
import com.hansollee.mydays.R
import com.hansollee.mydays.models.UniqueTask

/**
 * Created by kevin-ee on 2019-02-06.
 */

class TaskPickerDialog : DialogFragment(), TaskPickerListAdapter.ItemClickListener {

    interface Listener {
        fun onTaskPicked(uniqueTask: UniqueTask)
    }

    private var listener: Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_task_desc_picker, container, false)
        val tasksViewModel = TasksViewModel.getInstance(activity, GlobalViewModel.getInstance(activity).getTodayValue())
        val loadingView: View = view.findViewById(R.id.loading_view)
        val taskRecyclerview: RecyclerView = view.findViewById(R.id.task_list)
        val adapter = TaskPickerListAdapter(context, this)
        val emptyView: View = view.findViewById(R.id.empty_view)

        taskRecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        taskRecyclerview.adapter = adapter

        tasksViewModel.getAllUniqueTasks().observe(this, Observer<List<UniqueTask>> { items ->
            loadingView.visibility = View.GONE

            if (items.isEmpty()) {
                emptyView.visibility = View.VISIBLE
                taskRecyclerview.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                taskRecyclerview.visibility = View.VISIBLE
            }

            adapter.updateItems(items)
        })

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog.also {
            it.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setCanceledOnTouchOutside(false)
        }
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun onItemClick(uniqueTask: UniqueTask) {
        listener?.onTaskPicked(uniqueTask)
        dismiss()
    }

}