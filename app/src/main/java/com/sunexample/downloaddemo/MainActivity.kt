package com.sunexample.downloaddemo

import android.Manifest
import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadSerialQueue
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener1
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import com.sunexample.downloaddemo.Const.FIRENAME
import com.sunexample.downloaddemo.Const.FIREURL
import com.sunexample.downloaddemo.ListenerManager.manager
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.Exception

val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    private val taskurl = listOf(
        "http://imtt.dd.qq.com/16891/apk/39BE735C53CB0A73A37D841A49C6637C.apk?fsname=com.tencent.mm_7.0.15_1680.apk&csr=db5e"
        ,
        "http://122.246.10.36/imtt.dd.qq.com/16891/apk/C3C0305826223C72EBDF0BE4F7CDCD62.apk?mkey=5ef18c35b78d2fd2&f=8ea4&fsname=com.netease.cloudmusic_7.1.71_7001071.apk&csr=db5e&cip=183.141.9.39&proto=http"
        ,
        "http://dl.hdslb.com/mobile/latest/iBiliPlayer-bilibili140.apk"
        ,
        "http://122.246.10.36/imtt.dd.qq.com/16891/apk/7811D2FD0459429C4ED6916EC4879B28.apk?mkey=5ef18de3b78d2fd2&f=0af0&fsname=com.sina.weibo_10.6.2_4487.apk&csr=db5e&cip=183.141.9.39&proto=http"
        ,
        "http://dl-cdn.coolapkmarket.com/down/apk_upload/2020/0616/47d9131d79e3c8dd120c3c9abd23896b-0-o_1eatiisd313h51j8c129f1e1ri5h6-uid-408649.apk?_upt=fc1acf9d1592902849"
    )


    private val taskname = listOf("微信.apk", "网易云音乐.apk", "bilbil.apk", "Sina.apk", "酷安.apk")

    private var curtask = 4


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DownloadTaskManager.initManager(this)


        RxPermissions(this).requestEach(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
        ).subscribe()

        tasklist.setOnClickListener {
            startActivity(Intent(this, TaskListActivity::class.java))
        }

        add_task.setOnClickListener {
//            if (curtask < 4) {
//                val i = Intent(this, TaskService::class.java)
//                i.putExtra(FIREURL, taskurl[curtask])
//                i.putExtra(FIRENAME, taskname[curtask])
//                startService(i)

            var task = DownloadTask.Builder(taskurl[curtask], DownloadTaskManager.parentFile!!)
                .setFilename(taskname[curtask])
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(1000)
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(false)
                .build()

            DownloadTaskManager.DownloadTaskQueue.add(task)

            manager.attachListener(task, listener);
            manager.enqueueTaskWithUnifiedListener(task, listener);

//            task.enqueue(listener);

//                curtask++
//            }
        }

        pause_task.setOnClickListener {
            DownloadTaskManager.DownloadTaskQueue.get(0).cancel()
        }

        remove_task.setOnClickListener {

        }

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

}