package com.hansollee.mydays.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hansollee.mydays.R
import com.hansollee.mydays.models.History
import com.hansollee.mydays.toStringFormat
import com.hansollee.mydays.widgets.HistoryGraphView

/**
 * Created by kevin-ee on 2019-02-02.
 */

class HistoryListAdapter(context: Context,
                        private val historyFragViewModel: HistoryFragmentViewModel,
                        private val itemClickListener: HistoryItemClickListener)
    : RecyclerView.Adapter<HistoryListAdapter.HistoryItemViewHolder>() {

    interface HistoryItemClickListener {
        fun onItemClick(history: History)
    }

    private val inflater = LayoutInflater.from(context)
    private val historyItems: ArrayList<History> = ArrayList()
    @ColorInt private val defaultGraphColor = ContextCompat.getColor(context, R.color.default_history_graph_color)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HistoryItemViewHolder {
        return HistoryItemViewHolder.create(inflater, parent)
    }

    override fun getItemCount(): Int = historyItems.size

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bind(historyItems[position], itemClickListener, defaultGraphColor)
    }

    fun updateHistoryItems(items: List<History>) {
        historyItems.clear()
        historyItems.addAll(items)
        notifyDataSetChanged()
    }

    class HistoryItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(inflater: LayoutInflater, parent: ViewGroup?)
                = HistoryItemViewHolder(inflater.inflate(R.layout.view_history_item, parent, false))
        }

        private val date: TextView = view.findViewById(R.id.date)
        private val graph: HistoryGraphView = view.findViewById(R.id.graph)

        fun bind(history: History, itemClickListener: HistoryItemClickListener,
                 @ColorInt defGraphColor: Int) {
            date.text = history.date.toStringFormat()
            graph.setDefaultColor(defGraphColor)
            graph.drawHistory(history)
        }

    }
}
