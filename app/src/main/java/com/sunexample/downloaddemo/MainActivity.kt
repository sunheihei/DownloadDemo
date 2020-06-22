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
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.Exception


class MainActivity : AppCompatActivity() {


    private val taskurl = listOf(
        "https://d-15.winudf.com/b/APK/Y29tLm1vYmlsZS5sZWdlbmRzXzE0ODc1MjkyXzQ3MGY2ZTZh?_fn=TW9iaWxlIExlZ2VuZHMgQmFuZyBCYW5nX3YxLjQuODcuNTI5Ml9hcGtwdXJlLmNvbS5hcGs&_p=Y29tLm1vYmlsZS5sZWdlbmRz&am=KCbjHkuDP5spHiH6OW3keQ&at=1592806142&k=edc3afc8792d5ee1b215d1cec752c33b5ef19c7f"
        ,
        "https://d-10.winudf.com/b/XAPK/Y29tLm5ldGVhc2UuZzc4bmEuZ2JfMjAzX2FiZjFmMGUw?_fn=T25teW9qaSBBcmVuYV92My43Ni4wX2Fwa3B1cmUuY29tLnhhcGs&_p=Y29tLm5ldGVhc2UuZzc4bmEuZ2I&am=1W2rXg08ETpqL_U0OXdg4A&at=1592806552&k=9b889a6f01e7b8102c43929ad9c55ffe5ef19e18"
        ,
        "https://d-12.winudf.com/b/XAPK/Y29tLm5ldGVhc2UubXJ6aG5hXzE2NF80ZDQxNGM1Ng?_fn=TGlmZUFmdGVyX3YxLjAuMTY0X2Fwa3B1cmUuY29tLnhhcGs&_p=Y29tLm5ldGVhc2UubXJ6aG5h&am=o_--MVdHGE89hy8PRv-iYg&at=1592806637&k=1eade325e7a5c66856f6d349e87502415ef19e6e"
    )


    private val taskname = listOf("对决传说", "决战，平安京", "明日之后")

    private var curtask = 0
    val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RxPermissions(this).requestEach(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
        ).subscribe()

        tasklist.setOnClickListener {
            startActivity(Intent(this, TaskListActivity::class.java))
        }

        add_task.setOnClickListener {
//            if (curtask < 3) {
            val parentFile: File? = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

//                Log.d(TAG, "${parentFile!!.path}")

            var task = DownloadTask.Builder(taskurl[curtask], parentFile!!)
                .setFilename(taskname[curtask])
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(30)
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(false)
                .build()

            DownloadTaskManager.DownloadTaskQueue.add(task)
            task.enqueue(listener);

//            curtask++
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
            Log.d(TAG, "taskEnd : ${task.filename}")
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