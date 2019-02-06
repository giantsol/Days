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
import com.hansollee.mydays.models.TaskDescription

/**
 * Created by kevin-ee on 2019-02-06.
 */
class TaskDescListAdapter(context: Context,
                          private val taskFragViewModel: TaskFragmentViewModel,
                          private val itemClickListener: ItemClickListener)
    : RecyclerView.Adapter<TaskDescListAdapter.ItemViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(taskDescription: TaskDescription)
    }

    private val inflater = LayoutInflater.from(context)
    private val taskDescs: ArrayList<TaskDescription> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        return ItemViewHolder.create(inflater, parent)
    }

    override fun getItemCount(): Int = taskDescs.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(taskDescs[position], itemClickListener, taskFragViewModel)
    }

    fun setItems(items: List<TaskDescription>) {
        taskDescs.clear()
        taskDescs.addAll(items)
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(inflater: LayoutInflater, parent: ViewGroup?)
                = ItemViewHolder(inflater.inflate(R.layout.view_task_desc_item, parent, false))
        }

        private val contentContainer: View = view.findViewById(R.id.content_container)
        private val thumbnail: ImageView = view.findViewById(R.id.thumbnail)
        private val taskDescription: TextView = view.findViewById(R.id.task_description)

        fun bind(item: TaskDescription, itemClickListener: ItemClickListener, viewModel: TaskFragmentViewModel) {
            if (item.colorInt == 0) {
                (thumbnail.drawable.mutate() as ColorDrawable).color = viewModel.defaultTaskColor
            } else {
                (thumbnail.drawable.mutate() as ColorDrawable).color = item.colorInt
            }

            taskDescription.text = item.desc

            contentContainer.setOnClickListener { _ ->
                itemClickListener.onItemClick(item)
            }
        }

    }
}
