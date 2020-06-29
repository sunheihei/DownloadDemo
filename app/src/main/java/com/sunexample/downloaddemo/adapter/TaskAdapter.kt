package com.sunexample.downloaddemo.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.liulishuo.okdownload.StatusUtil
import com.sunexample.downloaddemo.*
import com.sunexample.downloaddemo.TaskBean.Task


class TaskAdapter(val mcontext: Context, var data: List<Task>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TaskViewHolder) {
            holder.task_name.text = data[position].name
            holder.tv_curoffset.text = byteToString(data[position].currentOffset)
            holder.tv_totallength.text = byteToString(data[position].totalLength)

            var status = StatusUtil.getStatus(DownloadTaskManager.DownloadTaskQueue[position])

            when (status) {
                StatusUtil.Status.PENDING -> {
                    holder.task_status.text = "PENDING"
                }
                StatusUtil.Status.RUNNING -> {
                    holder.task_status.text = "RUNNING"
                }
                StatusUtil.Status.COMPLETED -> {
                    holder.task_status.text = "COMPLETED"
                }
                StatusUtil.Status.IDLE -> {
                    holder.task_status.text = "IDLE"
                }
                StatusUtil.Status.UNKNOWN -> {
                    holder.task_status.text = "UNKNOWN"
                }
            }
            Log.d(TAG, "status：${status}")

            holder.task_root.setOnClickListener {
                if (status == StatusUtil.Status.IDLE || status == StatusUtil.Status.UNKNOWN) {
                    holder.task_status.text = "RUNNING"
                    mcontext.startService(
                        Intent(
                            mcontext,
                            TaskService::class.java
                        ).setAction(Const.TAG_RESTART_TASK)
                            .putExtra(Const.TAG_TASK, DownloadTaskManager.CusTomTaskQueue[position])
                    )
                } else if (status == StatusUtil.Status.RUNNING || status == StatusUtil.Status.PENDING) {
                    holder.task_status.text = "IDLE"
                    mcontext.startService(
                        Intent(
                            mcontext,
                            TaskService::class.java
                        ).setAction(Const.TAG_STOP_TASK)
                            .putExtra(Const.TAG_TASK, DownloadTaskManager.CusTomTaskQueue[position])
                    )
                }
            }

            holder.btn_more.setOnClickListener {
                if (mcontext is Activity) {
                    val dialog = BottomSheetDialog(mcontext)
                    val view: View =
                        mcontext.getLayoutInflater().inflate(R.layout.dialog_bottom_sheet, null)

                    val delete = view.findViewById(R.id.delete) as TextView

                    delete.setOnClickListener {
                        DownloadTaskManager.SynchizeWhenDelete(position)
                        dialog.dismiss()
                        notifyDataSetChanged()
                    }

                    dialog.setContentView(view)
                    dialog.show()
                }
            }


        }
    }


    class TaskViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        val task_name: TextView = itemView.findViewById(R.id.task_name)
        val task_status: TextView = itemView.findViewById(R.id.task_status)
        val task_root: RelativeLayout = itemview.findViewById(R.id.task_root)
        val tv_curoffset: TextView = itemview.findViewById(R.id.tv_curoffset)
        val tv_totallength: TextView = itemview.findViewById(R.id.tv_totallength)
        val btn_more: ImageView = itemview.findViewById(R.id.btn_more)

        companion object {
            fun create(parent: ViewGroup) =
                TaskViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_item_layout, parent, false)
                )
        }
    }


}