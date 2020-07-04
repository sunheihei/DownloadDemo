package com.sunexample.downloaddemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sunexample.downloaddemo.*
import com.sunexample.downloaddemo.TaskBean.Task


class TitleAdapter(var position1: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TitleViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TitleViewHolder) {
            if (position1 == 0) {
                holder.title.text = "Donwloading Task"
            } else if (position1 == 2) {
                holder.title.text = "Completed"
            }
        }
    }
}


class TitleViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    val title: TextView = itemView.findViewById(R.id.title)

    companion object {
        fun create(parent: ViewGroup) =
            TitleViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.title_task_item_layout, parent, false)
            )
    }
}

