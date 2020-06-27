package com.sunexample.downloaddemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener1
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import com.sunexample.downloaddemo.Const.NAME
import com.sunexample.downloaddemo.Const.URL
import com.sunexample.downloaddemo.eventbus.TaskEndEvent
import com.sunexample.downloaddemo.eventbus.TaskProgressEvent
import com.sunexample.downloaddemo.eventbus.TaskStartEvent
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

class TaskService : Service() {

    var FireName: String? = null
    var FireUrl: String? = null


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service Start")
        initService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        intent?.let {
            FireUrl = intent.getStringExtra(URL)
            FireName = intent.getStringExtra(NAME)
        }


        var task = DownloadTask.Builder(FireUrl!!, DownloadTaskManager.getParentFile())
            .setFilename(FireName)
            .setConnectionCount(1)
            // the minimal interval millisecond for callback progress
            .setMinIntervalMillisCallbackProcess(1000)
            // do re-download even if the task has already been completed in the past.
            .setPassIfAlreadyCompleted(false)
            .setWifiRequired(true)
            .build()

        DownloadTaskManager.addTaskToDownloadQueue(task)

        task.enqueue(listener);

        return START_NOT_STICKY
    }


    private fun initService() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "downloading",
                "Downloading",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, "downloading")
            .setContentTitle("DownloadDemo")
            .setContentText("Downloading...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        startForeground(1, notification)
    }


    val listener = object : DownloadListener1() {
        override fun taskStart(task: DownloadTask, model: Listener1Assist.Listener1Model) {
            Log.d(TAG, "taskStart : ${task.filename}")
            EventBus.getDefault().post(TaskStartEvent(task))
        }

        override fun taskEnd(
            task: DownloadTask,
            cause: EndCause,
            realCause: Exception?,
            model: Listener1Assist.Listener1Model
        ) {
            Log.d(TAG, "taskEnd : ${task.filename} cause: ${cause}")
            EventBus.getDefault().post(TaskEndEvent(task, cause, realCause))

            when (cause) {
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

        override fun progress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
//            Log.d(TAG, "progress : ${task.filename} ${currentOffset}")
            if (isForeground(this@TaskService, "TaskListActivity")) {
                EventBus.getDefault().post(
                    TaskProgressEvent(
                        task,
                        currentOffset,
                        totalLength
                    )
                )
            }
        }

        override fun connected(
            task: DownloadTask,
            blockCount: Int,
            currentOffset: Long,
            totalLength: Long
        ) {
            Log.d(TAG, "connected : ${task.filename}")
        }

        override fun retry(task: DownloadTask, cause: ResumeFailedCause) {
            Log.d(TAG, "retry : ${task.filename}")
        }

    }


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }


    override fun onDestroy() {
        OkDownload.with().downloadDispatcher().cancelAll();
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

}
