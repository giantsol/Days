package com.hansollee.mydays.record

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hansollee.mydays.R
import com.hansollee.mydays.models.Record
import com.hansollee.mydays.toStringFormat

/**
 * Created by kevin-ee on 2019-02-01.
 */

class RecordListAdapter(context: Context,
                        private val recordFragViewModel: RecordFragmentViewModel,
                        private val itemClickListener: RecordItemClickListener)
    : RecyclerView.Adapter<RecordListAdapter.RecordItemViewHolder>() {

    interface RecordItemClickListener {
        fun onItemClick(record: Record)
    }

    private val inflater = LayoutInflater.from(context)
    private val records: ArrayList<Record> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecordItemViewHolder {
        return RecordItemViewHolder.create(inflater, parent)
    }

    override fun getItemCount(): Int = records.size

    override fun onBindViewHolder(holder: RecordItemViewHolder, position: Int) {
        holder.bind(records[position], itemClickListener)
    }

    fun setRecords(newRecords: List<Record>) {
        records.clear()
        records.addAll(newRecords)
        notifyDataSetChanged()
    }

    class RecordItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(inflater: LayoutInflater, parent: ViewGroup?)
                = RecordItemViewHolder(inflater.inflate(R.layout.view_record_item, parent, false))
        }

        private val fromTime: TextView = view.findViewById(R.id.from_time)
        private val toTime: TextView = view.findViewById(R.id.to_time)
        private val taskDescription: TextView = view.findViewById(R.id.task_description)

        fun bind(record: Record, itemClickListener: RecordItemClickListener) {
            fromTime.text = record.fromTime.toStringFormat()
            toTime.text = record.toTime.toStringFormat()
            taskDescription.text = record.task

            view.setOnClickListener { _ ->
                itemClickListener.onItemClick(record)
            }
        }

    }
}