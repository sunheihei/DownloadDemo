package com.sunexample.downloaddemo.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sunexample.downloaddemo.*
import com.sunexample.downloaddemo.taskbean.Task
import java.io.File


class CompletedTaskAdapter(private val mContext: Context, var data: List<Task>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CompletedTaskViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CompletedTaskViewHolder) {
            holder.task_name.text = data[position].name
            holder.task_size.text = formatSize(
                mContext,
                getFileSize(
                    File(
                        DownloadTaskManager.getParentFile(),
                        data[position].name
                    )
                ).toString()
            )

            holder.task_root.setOnClickListener {

            }

            holder.btn_more.setOnClickListener {
                if (mContext is Activity) {
                    val dialog = BottomSheetDialog(mContext)
                    val view: View =
                        mContext.layoutInflater.inflate(R.layout.dialog_bottom_sheet, null)

                    val delete = view.findViewById(R.id.delete) as TextView

                    delete.setOnClickListener {
                        DownloadTaskManager.synchronizeWhenDeleteCompleted(position)
                        dialog.dismiss()
                        notifyDataSetChanged()
                    }

                    dialog.setContentView(view)
                    dialog.show()
                }
            }
        }
    }
}


class CompletedTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val task_name: TextView = itemView.findViewById(R.id.task_name)
    val task_size: TextView = itemView.findViewById(R.id.task_size)
    val task_root: RelativeLayout = itemView.findViewById(R.id.task_root)
    val btn_more: ImageView = itemView.findViewById(R.id.btn_more)

    companion object {
        fun create(parent: ViewGroup) =
            CompletedTaskViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.completed_task_item_layout, parent, false)
            )
    }
}

