package com.sunexample.downloaddemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        EventBus.getDefault().register(this);

        initRecycle()

    }

    private fun initRecycle() {
        adapter = TaskAdapter(DownloadTaskManager.CusTomTaskQueue)
        rec_tasklist.adapter = adapter
    }


    /**
     * 下载进度回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveTaskProgress(taskinfo: TaskProgressEvent) {
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
        when (taskend.cause) {
            EndCause.COMPLETED -> {
                //完成任务
            }
            EndCause.CANCELED -> {
                //取消
            }
            EndCause.ERROR -> {
                //网络中断
            }
            EndCause.SAME_TASK_BUSY -> {
                //重复任务
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}