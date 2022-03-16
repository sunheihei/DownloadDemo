package com.sunexample.downloaddemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sunexample.downloaddemo.taskbean.Task
import kotlinx.android.synthetic.main.activity_main.*

val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    private val taskurl = listOf(
        "https://www.wandoujia.com/apps/596157/download/dot?ch=detail_normal_dl",
        "https://www.wandoujia.com/apps/566489/binding?source=web_seo_others_binded",
        "https://www.wandoujia.com/apps/293217/binding?source=web_seo_others_binded",
        "https://www.wandoujia.com/apps/281291/download/dot?ch=detail_normal_dl",
        "https://www.wandoujia.com/apps/7519922/download/dot?ch=detail_normal_dl",
        "https://www.wandoujia.com/apps/280851/download/dot?ch=detail_normal_dl"
    )


    private val taskname =
        listOf("微信.apk", "QQ.apk", "网易云音乐.apk", "bilbil.apk", "Sina.apk", "百度网盘.apk")


    private var curtask = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DownloadTaskManager.initManager(this)


        //Android Q直接使用了应用沙盒存储
//        RxPermissions(this).requestEach(
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
//        ).subscribe()

        tasklist.setOnClickListener {
            startActivity(Intent(this, TaskListActivity::class.java))
        }


        add_task.setOnClickListener {
            if (curtask < 7) {
                //添加并启动任务
                DownloadTaskManager.startNewTask(
                    this,
                    Task(
                        taskname[curtask],
                        taskurl[curtask],
                        ""
                    )
                )
                curtask++
            }
        }

        stop_download.setOnClickListener {
            DownloadTaskManager.stopDownload(this)
        }

        pause_all_task.setOnClickListener {
            DownloadTaskManager.pauseAllTasks(this)
        }

        start_all_task.setOnClickListener {
            DownloadTaskManager.startAllTasks(this)
        }

    }
}