package com.sunexample.downloaddemo

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.text.TextUtils
import android.text.format.Formatter
import java.io.File
import java.io.FileInputStream


fun isForeground(context: Context?, className: String?): Boolean {
    if (context == null || TextUtils.isEmpty(className)) return false
    val am =
        context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val list = am.getRunningTasks(1)
    for (taskInfo in list) {
        if (taskInfo.topActivity!!.shortClassName.contains(className!!)) { // 说明它已经启动了
            return true
        }
    }
    return false
}


fun isServiceRunning(context: Context, className: String): Boolean {
    if (className.isNullOrEmpty()) {
        return false
    }
    var isRunning: Boolean = false
    var activityManager: ActivityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    var serviceList: List<ActivityManager.RunningServiceInfo> =
        activityManager.getRunningServices(30)
    if (serviceList.isEmpty()) {
        return false
    }
    serviceList.forEach { item ->
        if (item.service.className == className) {
            isRunning = true
            return@forEach
        }
    }
    return isRunning
}


/**
 * 获取指定文件大小
 * @param f
 * @return
 * @throws Exception 　　
 */
fun getFileSize(file: File): Long {
    var size: Long = 0
    if (file.exists()) {
        var fis = FileInputStream(file);
        size = fis.available().toLong();
    }
    return size;
}


fun formatSize(context: Context, target_size: String): String? {
    return Formatter.formatFileSize(context, java.lang.Long.valueOf(target_size))
}

