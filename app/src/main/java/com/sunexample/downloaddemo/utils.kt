package com.sunexample.downloaddemo

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.text.TextUtils
import java.text.DecimalFormat


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


fun isServiceRunning(context: Context, className: String): Boolean{
    if(className.isNullOrEmpty()){
        return false
    }
    var isRunning: Boolean = false
    var activityManager: ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    var serviceList: List<ActivityManager.RunningServiceInfo> = activityManager.getRunningServices(30)
    if(serviceList.isEmpty()){
        return false
    }
    serviceList.forEach { item ->
        if(item.service.className == className){
            isRunning = true
            return@forEach
        }
    }
    return isRunning
}


fun byteToString(size: Long): String {

    val GB : Long = 1024 * 1024 * 1024;//定义GB的计算常量
    val MB : Long= 1024 * 1024;//定义MB的计算常量
    val KB : Long= 1024;//定义KB的计算常量
    var df = DecimalFormat("0.00");//格式化小数
    var resultSize: String = "";
    if (size / GB >= 1) {
        //如果当前Byte的值大于等于1GB
        resultSize = df.format(size / GB) + " GB";
    } else if (size / MB >= 1) {
        //如果当前Byte的值大于等于1MB
        resultSize = df.format(size / MB) + " MB";
    } else if (size / KB >= 1) {
        //如果当前Byte的值大于等于1KB
        resultSize = df.format(size / KB) + " KB";
    } else {
        resultSize = " ${size} B ";
    }
    return resultSize;
}

