package com.sunexample.downloaddemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.StatusUtil
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener1
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import kotlinx.android.synthetic.main.activity_task_list.*
import java.lang.Exception

class TaskListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)


        task_restart.setOnClickListener {
            ListenerManager.manager.attachListener(DownloadTaskManager.DownloadTaskQueue.get(0), listener)

//            DownloadTaskManager.DownloadTaskQueue.get(0).enqueue(listener)

        }
    }


    val listener = object : DownloadListener1() {
        override fun taskStart(task: DownloadTask, model: Listener1Assist.Listener1Model) {
            Log.d(TAG, "taskStart2 : ${task.filename}")
        }

        override fun taskEnd(
            task: DownloadTask,
            cause: EndCause,
            realCause: Exception?,
            model: Listener1Assist.Listener1Model
        ) {
            Log.d(TAG, "taskEnd2 : ${task.filename} cause: ${cause}")
        }

        override fun progress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
            Log.d(TAG, "progress2 : ${task.filename} ${currentOffset}")
        }

        override fun connected(
            task: DownloadTask,
            blockCount: Int,
            currentOffset: Long,
            totalLength: Long
        ) {
            Log.d(TAG, "connected2 : ${task.filename}")
        }

        override fun retry(task: DownloadTask, cause: ResumeFailedCause) {
            Log.d(TAG, "retry2 : ${task.filename}")
        }

    }

}