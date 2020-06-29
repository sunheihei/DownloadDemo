package com.sunexample.downloaddemo

import android.app.*
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
import com.sunexample.downloaddemo.Const.TASK_TAG_KEY
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

    private var downloading_task = 0
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
                    task.addTag(TASK_TAG_KEY, it.tag)
                    //添加一条新任务到下载任务列表
                    DownloadTaskManager.addTaskToDownloadQueue(task)
                    task.enqueue(listener);
                }
            }
            //重新启动下载列表中的某个任务
            if (it.action == Const.TAG_RESTART_TASK) {
                var mTask: Task = it.getParcelableExtra(TAG_TASK)
                DownloadTaskManager.DownloadTaskQueue.forEach {
                    if (it.getTag(Const.TASK_TAG_KEY).equals(mTask.tag)) {
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
                    if (it.getTag(Const.TASK_TAG_KEY) == mTask.tag) {
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


        val intent = Intent(this, TaskListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, 0)

        notification = NotificationCompat.Builder(this, "downloading")
            .setContentTitle("DownloadDemo")
//            .setContentText("There are currently ${downloading_task} tasks downloading...")
            .setContentText("downloading...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
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

            var position = 0
            DownloadTaskManager.DownloadTaskQueue.forEach {
//               Log.d(TAG, "it.getTag(TASK_TAG_KEY) :" + it.getTag(TASK_TAG_KEY))
                if (it.getTag(TASK_TAG_KEY).equals(task.getTag(TASK_TAG_KEY))) {
                    position = DownloadTaskManager.DownloadTaskQueue.indexOf(it)
                }
            }

            when (cause) {
                EndCause.COMPLETED -> {
                    //完成任务,同步数据（删除完成的任务）
                    DownloadTaskManager.SynchronizeTask(position)
                }
                EndCause.CANCELED -> {
                    //取消
                    DownloadTaskManager.SynchronizeProgrss(position)
                }
                EndCause.ERROR -> {
                    //网络中断
                    DownloadTaskManager.SynchronizeProgrss(position)
                }
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
//          Log.d(TAG, "progress : ${task.filename} ${currentOffset}")
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
            Log.d(TAG, "connected : ${task.filename}  downloading_task: ${downloading_task}")
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
