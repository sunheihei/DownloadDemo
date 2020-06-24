package com.sunexample.downloaddemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liulishuo.okdownload.DownloadTask
import com.sunexample.downloaddemo.R

class TaskAdapter(var data: List<DownloadTask>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TaskViewHolder) {

        }
    }


    class TaskViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        val task_name: TextView = itemView.findViewById(R.id.task_name)
        val task_status: TextView = itemView.findViewById(R.id.task_status)
        val task_root: RelativeLayout = itemview.findViewById(R.id.task_root)
        val tv_curoffset: TextView = itemview.findViewById(R.id.tv_curoffset)
        val tv_totallength: TextView = itemview.findViewById(R.id.tv_totallength)

        companion object {
            fun create(parent: ViewGroup) =
                TaskViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_item_layout, parent, false)
                )
        }
    }


}