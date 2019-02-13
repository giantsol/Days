package com.hansollee.mydays.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hansollee.mydays.GlobalViewModel
import com.hansollee.mydays.R
import com.hansollee.mydays.getTotalDurationString
import com.hansollee.mydays.models.History
import com.hansollee.mydays.toDisplayFormat
import com.hansollee.mydays.widgets.CategoryView
import com.hansollee.mydays.widgets.HistoryGraphView

/**
 * Created by kevin-ee on 2019-02-02.
 */

class HistoryListAdapter(context: Context,
                        private val globalViewModel: GlobalViewModel,
                        private val itemClickListener: ItemClickListener): RecyclerView.Adapter<HistoryListAdapter.ItemViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(history: History)
    }

    private val inflater = LayoutInflater.from(context)
    private val items: ArrayList<History> = ArrayList()
    @ColorInt private val defaultGraphColor = ContextCompat.getColor(context, R.color.default_history_graph_color)

    class DiffCallback(val oldList: List<History>, val newList: List<History>): DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
            = oldList[oldItemPosition].date == newList[newItemPosition].date

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
            = oldList[oldItemPosition] == newList[newItemPosition]

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        return ItemViewHolder.create(inflater, parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].tasksGroupedByUnique.size
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position], itemClickListener, defaultGraphColor, globalViewModel)
    }

    fun updateItems(items: List<History>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(this.items, items))
        diffResult.dispatchUpdatesTo(this)

        this.items.clear()
        this.items.addAll(items)
    }

    class ItemViewHolder(view: View, private val categoryCount: Int) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(inflater: LayoutInflater, parent: ViewGroup?, categoryCount: Int)
                = ItemViewHolder(inflater.inflate(R.layout.view_history_item, parent, false), categoryCount)
        }

        private val contentContainer: View = view.findViewById(R.id.content_container)
        private val date: TextView = view.findViewById(R.id.date)
        private val graph: HistoryGraphView = view.findViewById(R.id.graph)
        private val categoriesContainer: ViewGroup = view.findViewById(R.id.categories_container)

        init {
            for (i in 1..categoryCount) {
                val category = CategoryView(view.context)
                categoriesContainer.addView(category)
            }
        }

        fun bind(history: History, itemClickListener: ItemClickListener,
                 @ColorInt defGraphColor: Int, globalViewModel: GlobalViewModel) {
            date.text = history.date.toDisplayFormat(globalViewModel.getTodayValue())
            graph.setDefaultColor(defGraphColor)
            graph.drawHistory(history)

            val groupedTasks = history.tasksGroupedByUnique
            val uniqueTasks = groupedTasks.keys
            for (i in 0.until(uniqueTasks.size)) {
                val categoryView = categoriesContainer.getChildAt(i) as CategoryView
                val uniqueTask = uniqueTasks.elementAt(i)
                val totalDurationOfUniqueTask: String = groupedTasks[uniqueTask]!!.getTotalDurationString(history.date)
                categoryView.update(uniqueTask.colorInt, uniqueTask.desc, totalDurationOfUniqueTask)
            }

            contentContainer.setOnClickListener { _ ->
                itemClickListener.onItemClick(history)
            }
        }

    }
}
