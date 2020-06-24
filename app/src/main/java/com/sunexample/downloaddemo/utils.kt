package com.sunexample.downloaddemo

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.text.TextUtils


fun isForeground(context: Context?, className: String?): Boolean {
    if (context == null || TextUtils.isEmpty(className)) return false
    val am =
        context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val list = am.getRunningTasks(1)
    //    boolean flag=false;
    for (taskInfo in list) {
        if (taskInfo.topActivity!!.shortClassName.contains(className!!)) { // 说明它已经启动了
//        flag = true;
            return true
        }
    }
    return false
}