package com.sunexample.downloaddemo

import android.app.DownloadManager
import android.content.ComponentName
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import com.liulishuo.okdownload.core.cause.EndCause
import com.sunexample.downloaddemo.adapter.CompletedTaskAdapter
import com.sunexample.downloaddemo.adapter.TaskAdapter
import com.sunexample.downloaddemo.adapter.TitleAdapter
import com.sunexample.downloaddemo.eventbus.TaskEndEvent
import com.sunexample.downloaddemo.eventbus.TaskProgressEvent
import com.sunexample.downloaddemo.eventbus.TaskStartEvent
import kotlinx.android.synthetic.main.activity_task_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TaskListActivity : AppCompatActivity() {

    private var adapter: TaskAdapter? = null
    private var completedadapter: CompletedTaskAdapter? = null
    private var mergeAdapter: MergeAdapter? = null

    //判断是否启动了service
    private var isServiceRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        EventBus.getDefault().register(this);

        adapter = TaskAdapter(this, DownloadTaskManager.CusTomTaskQueue)
        completedadapter = CompletedTaskAdapter(this, DownloadTaskManager.DownloadedTaskQueue)

        initRecycle()

        isServiceRunning = isServiceRunning(this, TaskService::class.java.name)
//        Log.d(TAG, "isServiceRunning:${isServiceRunning(this, TaskService::class.java.name)}")
    }


    override fun onResume() {
        super.onResume()
    }


    private fun initRecycle() {
        val layoutManager = GridLayoutManager(this, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == 0 || position == DownloadTaskManager.CusTomTaskQueue.size + 1) {
                    return 3;
                } else {
                    return 1
                }
            }
        }
        rec_tasklist.layoutManager = layoutManager
        mergeAdapter = MergeAdapter(TitleAdapter(0), adapter, TitleAdapter(2), completedadapter)
        rec_tasklist.adapter = mergeAdapter
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

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}