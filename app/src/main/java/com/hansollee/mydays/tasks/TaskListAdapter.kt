package com.hansollee.mydays.tasks

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
import com.hansollee.mydays.toEndTimeDisplayFormat
import com.hansollee.mydays.toStartTimeDisplayFormat

/**
 * Created by kevin-ee on 2019-02-01.
 */

class TaskListAdapter(context: Context,
                      private val itemClickListener: ItemClickListener,
                      private val tasksViewModel: TasksViewModel): RecyclerView.Adapter<TaskListAdapter.ItemViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(task: Task)
    }

    private val inflater = LayoutInflater.from(context)
    private val items: ArrayList<Task> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        return ItemViewHolder.create(inflater, parent)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position], itemClickListener, tasksViewModel)
    }

    fun updateItems(tasks: List<Task>) {
        items.clear()
        items.addAll(tasks)
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(inflater: LayoutInflater, parent: ViewGroup?)
                = ItemViewHolder(inflater.inflate(R.layout.view_task_item, parent, false))

            private const val TIME_RANGE_FORMAT = "%s - %s"
            private val PROCEEDING_TEXT = appContext?.getString(R.string.text_proceeding) ?: "Proceeding"
        }

        private val contentContainer: View = view.findViewById(R.id.content_container)
        private val thumbnail: ImageView = view.findViewById(R.id.thumbnail)
        private val timeRange: TextView = view.findViewById(R.id.time_range)
        private val taskDescription: TextView = view.findViewById(R.id.task_description)
        private val proceedingView: View = view.findViewById(R.id.proceeding_view)

        fun bind(task: Task, itemClickListener: ItemClickListener, tasksViewModel: TasksViewModel) {
            val currentDate = tasksViewModel.getCurrentDateValue()

            (thumbnail.drawable.mutate() as ColorDrawable).color = task.colorInt

            timeRange.text = String.format(
                TIME_RANGE_FORMAT,
                task.startDateTime.toStartTimeDisplayFormat(currentDate),
                if (task.endDateTime == null) PROCEEDING_TEXT else task.endDateTime.toEndTimeDisplayFormat(currentDate)
            )
            taskDescription.text = task.desc

            if (task.endDateTime == null) {
                proceedingView.visibility = View.VISIBLE
            } else {
                proceedingView.visibility = View.INVISIBLE
            }

            contentContainer.setOnClickListener { _ ->
                itemClickListener.onItemClick(task)
            }
        }

    }
}