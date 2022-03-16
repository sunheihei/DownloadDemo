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
        "http://[240e:f7:c010:302:12::]/imtt.dd.qq.com/sjy.20003/16891/apk/3D57DA2082F0ED3EBBC6FB406DCACD91.apk?mkey=623071e900002605&f=0000&fsname=com.netease.cloudmusic_8.7.03_8007003.apk&csr=db5e&cip=240e:390:4622:5a1:a57c:e6dc:db3b:ecbf&proto=http",
        "http://[240e:f7:c010:302:12::]/imtt.dd.qq.com/sjy.20003/16891/apk/9C9BF58EFF541A1995ABDF7A1F0711FA.apk?mkey=6230703600002605&f=17c3&fsname=tv.danmaku.bili_6.63.0_6630300.apk&csr=db5e&cip=240e:390:4622:5a1:a57c:e6dc:db3b:ecbf&proto=http",
        "http://[240e:96c:6200:400:35::]/imtt.dd.qq.com/sjy.20003/16891/apk/5006E6D093585793EAD8B09916AF7522.apk?mkey=6230713300002605&f=24c3&fsname=com.sina.weibo_12.3.1_5467.apk&csr=db5e&cip=240e:390:4622:5a1:a57c:e6dc:db3b:ecbf&proto=http",
        "http://[240e:e1:f400:6:11::]/imtt.dd.qq.com/sjy.20003/16891/apk/904CED87F76813D768DA66DAC71B309E.apk?mkey=6230717b00002605&f=0000&fsname=com.baidu.netdisk_11.19.11_1623.apk&csr=db5e&cip=240e:390:4622:5a1:a57c:e6dc:db3b:ecbf&proto=http"
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