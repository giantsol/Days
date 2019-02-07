package com.hansollee.mydays.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hansollee.mydays.R
import com.hansollee.mydays.models.TaskDescription

/**
 * Created by kevin-ee on 2019-02-06.
 */

class TaskDescPickerDialog : DialogFragment(), TaskDescListAdapter.ItemClickListener {

    interface Listener {
        fun onTaskDescPicked(taskDescription: TaskDescription)
    }

    private var listener: Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_task_desc_picker, container, false)
        val taskFragViewModel = ViewModelProviders.of(activity).get(TaskFragmentViewModel::class.java)
        val loadingView: View = view.findViewById(R.id.loading_view)
        val taskDescList: RecyclerView = view.findViewById(R.id.task_desc_list)
        val taskDescListAdapter = TaskDescListAdapter(context, taskFragViewModel, this)
        taskDescList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        taskDescList.adapter = taskDescListAdapter

        taskFragViewModel.getAllTaskDescriptions().observe(this, Observer<List<TaskDescription>> { taskDescs ->
            loadingView.visibility = View.GONE
            taskDescList.visibility = View.VISIBLE
            taskDescListAdapter.setItems(taskDescs)
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

    override fun onItemClick(taskDescription: TaskDescription) {
        listener?.onTaskDescPicked(taskDescription)
        dismiss()
    }

}