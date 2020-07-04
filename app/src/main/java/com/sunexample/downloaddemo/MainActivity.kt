package com.sunexample.downloaddemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener1
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import com.sunexample.downloaddemo.TaskBean.Task
import kotlinx.android.synthetic.main.activity_main.*
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
        "https://dl-android.keepcdn.com/release/apk/keep_adhub_cpa__jinbang_05_6.43.0_71592a0f.apk"
        ,
        "http://imtt.dd.qq.com/16891/apk/EB8582F240BC4973BD34903B951F1740.apk?fsname=com.baidu.netdisk_10.1.31_1199.apk&csr=db5e"
        ,
        "http://m.down.sandai.net/MobileThunder/Android_6.23.2.6980/thunder-6.23.2.6980-stable-release-jiagu-fufei91.apk"
    )


    private val taskname =
        listOf("微信.apk", "网易云音乐.apk", "bilbil.apk", "Sina.apk", "keep.apk", "百度网盘.apk", "迅雷.apk")

    private var curtask = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DownloadTaskManager.initManager(this)


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
                DownloadTaskManager.StartNewTask(
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
            stopService(Intent(this, TaskService::class.java))
        }

        pause_all_task.setOnClickListener {
            startService(Intent(this, TaskService::class.java).setAction(Const.TAG_STOP_ALL_TASK))
        }

        start_all_task.setOnClickListener {
            startService(Intent(this, TaskService::class.java).setAction(Const.TAG_START_ALL_TASK))
        }

    }
}