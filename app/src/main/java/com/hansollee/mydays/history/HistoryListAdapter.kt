package com.hansollee.mydays.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansollee.mydays.R
import com.hansollee.mydays.models.History

/**
 * Created by kevin-ee on 2019-02-02.
 */

class HistoryListAdapter(context: Context,
                        private val recordFragViewModel: HistoryFragmentViewModel,
                        private val itemClickListener: HistoryItemClickListener)
    : RecyclerView.Adapter<HistoryListAdapter.HistoryItemViewHolder>() {

    interface HistoryItemClickListener {
        fun onItemClick(history: History)
    }

    private val inflater = LayoutInflater.from(context)
    private val historyItems: ArrayList<History> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HistoryItemViewHolder {
        return HistoryItemViewHolder.create(inflater, parent)
    }

    override fun getItemCount(): Int = historyItems.size

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bind(historyItems[position], itemClickListener)
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

        fun bind(history: History, itemClickListener: HistoryItemClickListener) {

        }

    }
}
