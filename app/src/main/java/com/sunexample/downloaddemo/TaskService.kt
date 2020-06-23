package com.sunexample.downloaddemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener1
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import com.sunexample.downloaddemo.Const.FIRENAME
import com.sunexample.downloaddemo.Const.FIREURL
import java.lang.Exception

class TaskService : Service() {

    var FireName: String? = null
    var FireUrl: String? = null

    override fun onCreate() {
        super.onCreate()
        DownloadTaskManager.initManager(this)
        Log.d(TAG, "Service Start")
        initService()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        intent?.let {
            FireUrl = intent.getStringExtra(FIREURL)
            FireName = intent.getStringExtra(FIRENAME)
        }
        var task = DownloadTask.Builder(FireUrl!!, DownloadTaskManager.parentFile!!)
            .setFilename(FireName)
            .setConnectionCount(1)
            // the minimal interval millisecond for callback progress
            .setMinIntervalMillisCallbackProcess(1000)
            // do re-download even if the task has already been completed in the past.
            .setPassIfAlreadyCompleted(false)
            .build()

        DownloadTaskManager.DownloadTaskQueue.add(task)

        task.enqueue(listener);

        return START_STICKY
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
        }

        override fun taskEnd(
            task: DownloadTask,
            cause: EndCause,
            realCause: Exception?,
            model: Listener1Assist.Listener1Model
        ) {
            Log.d(TAG, "taskEnd : ${task.filename} cause: ${cause}")

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
            Log.d(TAG, "progress : ${task.filename} ${currentOffset}")
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
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

}
