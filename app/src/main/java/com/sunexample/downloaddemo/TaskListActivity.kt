package com.sunexample.downloaddemo

import android.app.DownloadManager
import android.content.ComponentName
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.liulishuo.okdownload.core.cause.EndCause
import com.sunexample.downloaddemo.adapter.TaskAdapter
import com.sunexample.downloaddemo.eventbus.TaskEndEvent
import com.sunexample.downloaddemo.eventbus.TaskProgressEvent
import com.sunexample.downloaddemo.eventbus.TaskStartEvent
import kotlinx.android.synthetic.main.activity_task_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TaskListActivity : AppCompatActivity() {

    private var adapter: TaskAdapter? = null

    //判断是否启动了service
    private var isServiceRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        EventBus.getDefault().register(this);
        initRecycle()
        isServiceRunning = isServiceRunning(this, TaskService::class.java.name)
        Log.d(TAG, "isServiceRunning:${isServiceRunning(this, TaskService::class.java.name)}")
    }


    override fun onResume() {
        super.onResume()
    }


    private fun initRecycle() {
        adapter = TaskAdapter(this, DownloadTaskManager.CusTomTaskQueue)
        rec_tasklist.adapter = adapter
    }


    /**
     * 下载进度回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveTaskProgress(taskinfo: TaskProgressEvent) {
        DownloadTaskManager.CusTomTaskQueue.forEach {
            if (it.name.equals(taskinfo.task.filename)) {
                it.currentOffset = taskinfo.currentOffset
                it.totalLength = taskinfo.totalLength
                var position = DownloadTaskManager.CusTomTaskQueue.indexOf(it)
                adapter!!.notifyItemChanged(position)
            }
        }
        Log.e(TAG, "filename:${taskinfo.task.filename}");
        Log.e(TAG, "currentOffset:${taskinfo.currentOffset}");
        Log.e(TAG, "totalLength:”${taskinfo.totalLength}");
    }

    /**
     * 任务开始
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveTaskStart(taskstart: TaskStartEvent) {

    }

    /**
     * 任务结束（完成或错误）
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveTaskEnd(taskend: TaskEndEvent) {

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

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}