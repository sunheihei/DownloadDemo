package com.sunexample.downloaddemo

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.liulishuo.okdownload.core.cause.EndCause
import com.sunexample.downloaddemo.adapter.CompletedTaskAdapter
import com.sunexample.downloaddemo.adapter.TaskAdapter
import com.sunexample.downloaddemo.adapter.TitleAdapter
import kotlinx.android.synthetic.main.activity_task_list.*


class TaskListActivity : AppCompatActivity() {

    private var adapter: TaskAdapter? = null
    private var completedadapter: CompletedTaskAdapter? = null
    private var mergeAdapter: ConcatAdapter? = null

    //判断是否启动了service

    lateinit var myBinder: TaskService.MyBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)


        initRecycle()


        val bindIntent = Intent(this, TaskService::class.java)
        bindService(bindIntent, conn, BIND_AUTO_CREATE)
    }


    var conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            myBinder = service as TaskService.MyBinder

            myBinder.setOnStartDownload {
                Log.d(TAG, "setOnStartDownload")
            }
            myBinder.setOnDownloadProgress { taskinfo ->
                Log.d(TAG, "setOnDownloadProgress")
                DownloadTaskManager.CusTomTaskQueue.forEach {
                    if (it.name.equals(taskinfo.task.filename)) {
                        it.currentOffset = taskinfo.currentOffset
                        it.totalLength = taskinfo.totalLength
                        var position = DownloadTaskManager.CusTomTaskQueue.indexOf(it)
                        adapter!!.notifyItemChanged(position)
                    }
                }
            }

            myBinder.setOnEndDownload { taskend ->
                Log.d(TAG, "setOnEndDownload")
                var position = 0
                DownloadTaskManager.CusTomTaskQueue.forEach {
                    if (it.tag.equals(taskend.task.getTag(Const.TASK_TAG_KEY))) {
                        position = DownloadTaskManager.CusTomTaskQueue.indexOf(it)
                    }
                }
                when (taskend.cause) {
                    EndCause.COMPLETED -> {
                        //完成任务
                        adapter!!.notifyDataSetChanged()
                        completedadapter!!.notifyDataSetChanged()
                    }
                    EndCause.CANCELED -> {
                        //取消
                        adapter!!.notifyItemChanged(position)
                    }
                    EndCause.ERROR -> {
                        //网络中断
                        adapter!!.notifyItemChanged(position)
                    }
                    EndCause.SAME_TASK_BUSY -> {
                        //重复任务或者到达同时下载任务上限
                    }
                }
            }

        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    private fun initRecycle() {

        adapter = TaskAdapter(this, DownloadTaskManager.CusTomTaskQueue)

        adapter!!.setOnItemClick { task, action ->
            val intent = Intent(
                this,
                TaskService::class.java
            ).setAction(action)
                .putExtra(Const.TAG_TASK, task)
            Log.d(TAG, "BIND")
            bindService(intent, conn, BIND_AUTO_CREATE)
        }

        completedadapter = CompletedTaskAdapter(this, DownloadTaskManager.DownloadedTaskQueue)

        val layoutManager = GridLayoutManager(this, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0 || position == DownloadTaskManager.CusTomTaskQueue.size + 1) {
                    3
                } else {
                    1
                }
            }
        }
        rec_tasklist.layoutManager = layoutManager
        mergeAdapter = ConcatAdapter(TitleAdapter(0), adapter, TitleAdapter(2), completedadapter)
        rec_tasklist.adapter = mergeAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(conn)
    }

}