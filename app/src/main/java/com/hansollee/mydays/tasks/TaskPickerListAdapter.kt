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
import com.hansollee.mydays.models.UniqueTask

/**
 * Created by kevin-ee on 2019-02-06.
 */
class TaskPickerListAdapter(context: Context,
                            private val itemClickListener: ItemClickListener)
    : RecyclerView.Adapter<TaskPickerListAdapter.ItemViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(uniqueTask: UniqueTask)
    }

    private val inflater = LayoutInflater.from(context)
    private val items: ArrayList<UniqueTask> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        return ItemViewHolder.create(inflater, parent)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position], itemClickListener)
    }

    fun updateItems(items: List<UniqueTask>) {
        this.items.clear()
        this.items.addAll(items)
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

        fun bind(item: UniqueTask, itemClickListener: ItemClickListener) {
            (thumbnail.drawable.mutate() as ColorDrawable).color = item.colorInt
            taskDescription.text = item.desc
            contentContainer.setOnClickListener { _ ->
                itemClickListener.onItemClick(item)
            }
        }

    }
}
