package com.sunexample.downloaddemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sunexample.downloaddemo.taskbean.Task
import kotlinx.android.synthetic.main.activity_main.*

val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    private val taskurl = listOf(
        "https://ip3924883852.mobgslb.tbcache.com/fs08/2023/02/24/6/106_c81acab0bb7962043bbb46785e9e5279.apk?yingid=wdj_web&fname=%E5%BE%AE%E4%BF%A1&productid=2011&pos=wdj_web%2Fdetail_normal_dl%2F0&appid=596157&packageid=601162766&apprd=596157&iconUrl=http%3A%2F%2Fandroid-artworks.25pp.com%2Ffs08%2F2023%2F02%2F27%2F7%2F106_edec7b625709a55af80c20337fd36c79_con.png&pkg=com.tencent.mm&did=a27fd607cb084434cc6a007c598ce4f1&vcode=2320&md5=5ff1f8e88fc1a36bf82ed5179623831f&ali_redirect_domain=alissl.ucdl.pp.uc.cn&ali_redirect_ex_ftag=97e834b137623e8ab94fc1e868506512347c030561befe47&ali_redirect_ex_tmining_ts=1678245742&ali_redirect_ex_tmining_expire=3600&ali_redirect_ex_hot=100",
        "https://ip3978027621.mobgslb.tbcache.com/fs08/2023/03/07/8/110_b158468bb92f71f36a7af5c2f4ad457f.apk?yingid=wdj_web&fname=QQ&productid=2011&pos=wdj_web%2Fdetail_normal_dl%2F0&appid=566489&packageid=201175473&apprd=566489&iconUrl=http%3A%2F%2Fandroid-artworks.25pp.com%2Ffs08%2F2023%2F03%2F07%2F6%2F110_c6a6c2577af801c593dff7c633eb6eb3_con.png&pkg=com.tencent.mobileqq&did=a924d9a5327aa0a4d7e77ff5debc1936&vcode=3772&md5=f0b6fef3a2c1f156067c444ddac08c17&ali_redirect_domain=alissl.ucdl.pp.uc.cn&ali_redirect_ex_ftag=608f436aedb79559b99e4f1e76507b4505f440e3bb14351f&ali_redirect_ex_tmining_ts=1678245813&ali_redirect_ex_tmining_expire=3600&ali_redirect_ex_hot=100",
        "https://ip3978027621.mobgslb.tbcache.com/fs08/2023/03/01/1/106_8a2fcaeec29db84a33fef4902718b35d.apk?yingid=wdj_web&fname=%E7%BD%91%E6%98%93%E4%BA%91%E9%9F%B3%E4%B9%90&productid=2011&pos=wdj_web%2Fdetail_normal_dl%2F0&appid=293217&packageid=201174082&apprd=293217&iconUrl=http%3A%2F%2Fandroid-artworks.25pp.com%2Ffs08%2F2023%2F03%2F03%2F6%2F110_8e6cd367ae661065e61f27a9e2b560a2_con.png&pkg=com.netease.cloudmusic&did=8ba395c32a5a3f4850d9e71673f7dcea&vcode=8009040&md5=7edf8a7494aabcd17965d33afb1a4879&ali_redirect_domain=alissl.ucdl.pp.uc.cn&ali_redirect_ex_ftag=9ba0f8598a9bb37676f940a6d119c3846dfdf3e1ba002014&ali_redirect_ex_tmining_ts=1678245826&ali_redirect_ex_tmining_expire=3600&ali_redirect_ex_hot=100",
        "https://ip3759465322.mobgslb.tbcache.com/fs08/2023/03/01/6/2_69e857f4c51c7b10723bcb22a6e09df7.apk?yingid=wdj_web&fname=%E5%93%94%E5%93%A9%E5%93%94%E5%93%A9&productid=2011&pos=wdj_web%2Fdetail_normal_dl%2F0&appid=281291&packageid=401150104&apprd=281291&iconUrl=http%3A%2F%2Fandroid-artworks.25pp.com%2Ffs08%2F2023%2F03%2F03%2F0%2F110_371166cd60c1f69ac7a101bc9ea47a38_con.png&pkg=tv.danmaku.bili&did=3438b73b09bd0dffc3263e5042622ab8&vcode=7190300&md5=63f7568bce0b3f4fed215039697fad8b&ali_redirect_domain=alissl.ucdl.pp.uc.cn&ali_redirect_ex_ftag=c4f86f928fa0754934ebe593875d0f37975edd3ff7817e61&ali_redirect_ex_tmining_ts=1678245790&ali_redirect_ex_tmining_expire=3600&ali_redirect_ex_hot=100",
        "https://ip3520717178.mobgslb.tbcache.com/fs08/2023/03/01/0/106_157bb6968a60081ba395ec35353454ed.apk?yingid=wdj_web&fname=%E5%BE%AE%E5%8D%9A&productid=2011&pos=wdj_web%2Fdetail_normal_dl%2F0&appid=289255&packageid=801102989&apprd=289255&iconUrl=http%3A%2F%2Fandroid-artworks.25pp.com%2Ffs08%2F2023%2F03%2F03%2F11%2F110_8c33f26c0bc39fb528bb61092d1bc77c_con.png&pkg=com.sina.weibo&did=e6603b4f96002d06ab8d77e4ea44e8af&vcode=6064&md5=a02b838337468093d1c1e5aa549e4bfa&ali_redirect_domain=alissl.ucdl.pp.uc.cn&ali_redirect_ex_ftag=7cf367fb3bf54f2a0835e8e2736619955db1007276dc233c&ali_redirect_ex_tmining_ts=1678245836&ali_redirect_ex_tmining_expire=3600&ali_redirect_ex_hot=100",
        "https://ip3520717178.mobgslb.tbcache.com/fs08/2023/02/21/1/106_7b4c9d012981e350eba9f919c283cb13.apk?yingid=wdj_web&fname=%E7%99%BE%E5%BA%A6%E7%BD%91%E7%9B%98&productid=2011&pos=wdj_web%2Fdetail_normal_dl%2F0&appid=280851&packageid=401148884&apprd=280851&iconUrl=http%3A%2F%2Fandroid-artworks.25pp.com%2Ffs08%2F2023%2F02%2F23%2F0%2F110_d6e5eeba85fa401da0885bb534ab672c_con.png&pkg=com.baidu.netdisk&did=e857e96866d60c7611e14e20b049a81c&vcode=1902&md5=b1b925245d3ddd9124cb2fa8c20d08d1&ali_redirect_domain=alissl.ucdl.pp.uc.cn&ali_redirect_ex_ftag=a8b304928e195e271306af6203a0e69226a5b977ec0cf49d&ali_redirect_ex_tmining_ts=1678245844&ali_redirect_ex_tmining_expire=3600&ali_redirect_ex_hot=100"
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