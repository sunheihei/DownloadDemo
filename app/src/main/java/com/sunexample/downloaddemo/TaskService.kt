package com.sunexample.downloaddemo

import android.app.Notification
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
import com.sunexample.downloaddemo.Const.TAG_TASK
import com.sunexample.downloaddemo.TaskBean.Task
import com.sunexample.downloaddemo.eventbus.TaskEndEvent
import com.sunexample.downloaddemo.eventbus.TaskProgressEvent
import com.sunexample.downloaddemo.eventbus.TaskStartEvent
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

class TaskService : Service() {

    private val FOREGROUND_SERVICE = 101
    private var notification: Notification? = null
    private var manager: NotificationManager? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service Start")
        initService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        intent?.let {
            //新开启一个下载任务
            if (it.action == Const.TAG_START_NEW_TASK) {
                var mTask: Task = it.getParcelableExtra(TAG_TASK)
                mTask?.let {
                    var task =
                        DownloadTask.Builder(it.url, DownloadTaskManager.getParentFile())
                            .setFilename(it.name)
                            .setConnectionCount(1)
                            // the minimal interval millisecond for callback progress
                            .setMinIntervalMillisCallbackProcess(1000)
                            // do re-download even if the task has already been completed in the past.
                            .setPassIfAlreadyCompleted(false)
                            .setWifiRequired(true)
                            .build()
                    //添加一条新任务到下载任务列表
                    DownloadTaskManager.addTaskToDownloadQueue(task)
                    task.enqueue(listener);
                }
            }
            //重新启动下载列表中的某个任务
            if (it.action == Const.TAG_RESTART_TASK) {
                var mTask: Task = it.getParcelableExtra(TAG_TASK)
                DownloadTaskManager.DownloadTaskQueue.forEach {
                    if (it.filename == mTask.name) {
                        it.enqueue(listener)
                    }
                }
            }

            //启动全部任务下载
            if (it.action == Const.TAG_START_ALL_TASK) {
                if (DownloadTaskManager.DownloadTaskQueue.size != 0) {
                    DownloadTaskManager.DownloadTaskQueue.forEach {
                        it.enqueue(listener)
                    }
                }
            }

            //暂停某个任务
            if (it.action == Const.TAG_STOP_TASK) {
                var mTask: Task = it.getParcelableExtra(TAG_TASK)
                DownloadTaskManager.DownloadTaskQueue.forEach {
                    if (it.filename == mTask.name) {
                        it.cancel()
                    }
                }
            }
            //暂定全部任务,并且退出service
            if (it.action == Const.TAG_STOP_ALL_TASK) {
                if (DownloadTaskManager.DownloadTaskQueue.size != 0) {
                    DownloadTaskManager.DownloadTaskQueue.forEach {
                        it.cancel()
                    }
                }
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }


    private fun initService() {
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "downloading",
                "Downloading",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager!!.createNotificationChannel(channel)
        }
        notification = NotificationCompat.Builder(this, "downloading")
            .setContentTitle("DownloadDemo")
            .setContentText("Downloading...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        startForeground(FOREGROUND_SERVICE, notification)
    }


    val listener = object : DownloadListener1() {
        override fun taskStart(task: DownloadTask, model: Listener1Assist.Listener1Model) {
            Log.d(TAG, "taskStart : ${task.filename}")
            if (isForeground(this@TaskService, "TaskListActivity")) {
                EventBus.getDefault().post(TaskStartEvent(task))
            }
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
                    //完成任务,同步数据（删除完成的任务）
                    var position = 0
                    DownloadTaskManager.DownloadTaskQueue.forEach {
                        if (it.filename.equals(task.filename)) {
                            position = DownloadTaskManager.DownloadTaskQueue.indexOf(it)
                        }
                    }
                    DownloadTaskManager.SynchronizeTask(position)
                }
//                EndCause.CANCELED -> {
//                    //取消
//                }
//                EndCause.ERROR -> {
//                    //网络中断
//                }
                EndCause.SAME_TASK_BUSY -> {
                    //重复任务或者到达同时下载任务上限
                }
            }

            //如果下载界面可见，发送消息给界面
            if (isForeground(this@TaskService, "TaskListActivity")) {
                EventBus.getDefault().post(TaskEndEvent(task, cause, realCause))
            }

            //当任务列表中没有的时候就停止后台任务
            if (DownloadTaskManager.DownloadTaskQueue.size == 0) {
                stopSelf()
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
