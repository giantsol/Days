package com.hansollee.mydays.task

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hansollee.mydays.R
import com.hansollee.mydays.appContext
import com.hansollee.mydays.models.Task
import com.hansollee.mydays.toStringFormat

/**
 * Created by kevin-ee on 2019-02-01.
 */

class TaskListAdapter(context: Context,
                      private val taskFragViewModel: TaskFragmentViewModel,
                      private val itemClickListener: ItemClickListener)
    : RecyclerView.Adapter<TaskListAdapter.TaskItemViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(task: Task)
    }

    private val inflater = LayoutInflater.from(context)
    private val tasks: ArrayList<Task> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TaskItemViewHolder {
        return TaskItemViewHolder.create(inflater, parent)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        holder.bind(tasks[position], itemClickListener, taskFragViewModel)
    }

    fun setTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    class TaskItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(inflater: LayoutInflater, parent: ViewGroup?)
                = TaskItemViewHolder(inflater.inflate(R.layout.view_task_item, parent, false))

            private const val TIME_RANGE_FORMAT = "%s - %s"
            private val PROCEEDING_TEXT = appContext?.getString(R.string.text_proceeding) ?: "Proceeding"
        }

        private val contentContainer: View = view.findViewById(R.id.content_container)
        private val thumbnail: ImageView = view.findViewById(R.id.thumbnail)
        private val timeRange: TextView = view.findViewById(R.id.time_range)
        private val taskDescription: TextView = view.findViewById(R.id.task_description)

        fun bind(task: Task, itemClickListener: ItemClickListener, viewModel: TaskFragmentViewModel) {
            (thumbnail.drawable.mutate() as ColorDrawable).color = task.colorInt

            timeRange.text = String.format(TIME_RANGE_FORMAT,
                task.startTime.toStringFormat(),
                if (task.endTime == null) PROCEEDING_TEXT else task.endTime.toStringFormat())
            taskDescription.text = task.desc

            contentContainer.setOnClickListener { _ ->
                itemClickListener.onItemClick(task)
            }
        }

    }
}