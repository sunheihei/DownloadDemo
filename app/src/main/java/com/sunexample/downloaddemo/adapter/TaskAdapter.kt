package com.sunexample.downloaddemo.adapter

import android.app.Activity
import android.content.Context
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
import com.sunexample.downloaddemo.taskbean.Task


class TaskAdapter(private val mContext: Context, private var data: List<Task>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var itemClick: ((task: Task, action: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TaskViewHolder) {
            holder.task_name.text = data[position].name
            holder.tv_curoffset.text = formatSize(mContext, data[position].currentOffset.toString())
            holder.tv_totallength.text = formatSize(mContext, data[position].totalLength.toString())

            val status = StatusUtil.getStatus(DownloadTaskManager.DownloadTaskQueue[position])

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
                    itemClick?.invoke(
                        DownloadTaskManager.CusTomTaskQueue[position],
                        Const.TAG_RESTART_TASK
                    )

                } else if (status == StatusUtil.Status.RUNNING || status == StatusUtil.Status.PENDING) {
                    holder.task_status.text = "IDLE"
                    itemClick?.invoke(
                        DownloadTaskManager.CusTomTaskQueue[position],
                        Const.TAG_STOP_TASK
                    )
                }
            }

            holder.btn_more.setOnClickListener {
                if (mContext is Activity) {
                    val dialog = BottomSheetDialog(mContext)
                    val view: View =
                        mContext.layoutInflater.inflate(R.layout.dialog_bottom_sheet, null)

                    val delete = view.findViewById(R.id.delete) as TextView

                    delete.setOnClickListener {
                        DownloadTaskManager.synchronizeWhenDelete(position)
                        dialog.dismiss()
                        notifyDataSetChanged()
                    }

                    dialog.setContentView(view)
                    dialog.show()
                }
            }
        }
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val task_name: TextView = this.itemView.findViewById(R.id.task_name)
        val task_status: TextView = this.itemView.findViewById(R.id.task_status)
        val task_root: RelativeLayout = itemView.findViewById(R.id.task_root)
        val tv_curoffset: TextView = itemView.findViewById(R.id.tv_curoffset)
        val tv_totallength: TextView = itemView.findViewById(R.id.tv_totallength)
        val btn_more: ImageView = itemView.findViewById(R.id.btn_more)

        companion object {
            fun create(parent: ViewGroup) =
                TaskViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_item_layout, parent, false)
                )
        }
    }

    fun setOnItemClick(itemClick: (task: Task, action: String) -> Unit) {
        this.itemClick = itemClick
    }

}