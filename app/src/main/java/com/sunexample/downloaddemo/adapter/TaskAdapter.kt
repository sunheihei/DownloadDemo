package com.sunexample.downloaddemo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.StatusUtil
import com.sunexample.downloaddemo.DownloadTaskManager
import com.sunexample.downloaddemo.R
import com.sunexample.downloaddemo.TAG
import com.sunexample.downloaddemo.TaskBean.Task

class TaskAdapter(var data: List<Task>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TaskViewHolder) {
            holder.task_name.text = data[position].name
            holder.tv_curoffset.text = data[position].currentOffset.toString()
            holder.tv_totallength.text = data[position].totalLength.toString()

            var status = StatusUtil.getStatus(DownloadTaskManager.DownloadTaskQueue[position])

            when(status){
                StatusUtil.Status.PENDING->{holder.task_status.text = "PENDING"}
                StatusUtil.Status.RUNNING->{holder.task_status.text = "RUNNING"}
                StatusUtil.Status.COMPLETED->{holder.task_status.text = "COMPLETED"}
                StatusUtil.Status.IDLE->{holder.task_status.text = "IDLE"}
                StatusUtil.Status.UNKNOWN->{holder.task_status.text = "UNKNOWN"}
            }
            Log.d(TAG,"status：${status}")

            holder.task_root.setOnClickListener {

            }

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