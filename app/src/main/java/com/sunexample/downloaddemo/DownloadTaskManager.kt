package com.sunexample.downloaddemo

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import android.util.Log
import com.liulishuo.okdownload.DownloadTask
import com.sunexample.downloaddemo.Const.DBNAME
import com.sunexample.downloaddemo.Const.NAME
import com.sunexample.downloaddemo.Const.URL
import com.sunexample.downloaddemo.Const.TABLENAME
import com.sunexample.downloaddemo.sqlite.TaskDatabaseHelper
import java.io.File

object DownloadTaskManager {

    var db: SQLiteDatabase? = null

    var parentFile: File? = null

    var DownloadTaskQueue = mutableListOf<DownloadTask>()

    fun initManager(context: Context) {
        //确定路劲
        parentFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val dbhelper = TaskDatabaseHelper(context, DBNAME, 1)
        db = dbhelper.writableDatabase
        initTaskQueue()
    }


    fun StartTask(context: Context, FireName: String, FireUrl: String) {

        DownloadTaskQueue.forEach {
            if (it.filename == FireName && it.url == FireUrl) return
        }

        val i = Intent(context, TaskService::class.java)
        i.putExtra(URL, FireUrl)
        i.putExtra(NAME, FireName)
        context.startService(i)
    }


    fun initTaskQueue() {
        DownloadTaskQueue.addAll(getDataFromDatabase())
        Log.d(TAG, " DownloadTaskQueue.size : ${DownloadTaskQueue.size}")
    }


    fun addTask(task: DownloadTask) {
//        if (DownloadTaskQueue.size == 0) {
        DownloadTaskQueue.add(task)
        addTaskToDataBase(task)
//        }
//        DownloadTaskQueue.forEach {
//            if (task.filename == it.filename && task.url == it.url) {
////                && task.file!!.exists()
//                //存在相同任务
//                return
//            } else {
//                //如果不存在，存入列表和数据库`
//                DownloadTaskQueue.add(task)
//                addTaskToDataBase(task)
//            }
//        }
    }

    /**
     * 插入一条任务到数据库
     */
    private fun addTaskToDataBase(task: DownloadTask) {
        Log.d(TAG, "DownloadTaskQueue")
        val value = ContentValues().apply {
            put(Const.NAME, task.filename)
            put(Const.URL, task.url)
        }
        db!!.insert(Const.TABLENAME, null, value)
    }

    /**
     *删除一条任务
     */
    fun deleteTaskFromDateBase(task: DownloadTask) {
        db!!.delete(Const.TABLENAME, "$NAME  = ", arrayOf(task.filename))
    }


    /**
     * 从数据库查找全部任务，放进任务队列
     */
    fun getDataFromDatabase(): List<DownloadTask> {
        val tasklist = mutableListOf<DownloadTask>()
        val cursor = db?.query(TABLENAME, null, null, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(NAME))
                val fireurl = cursor.getString(cursor.getColumnIndex(URL))
//                Log.d(TAG, " name : ${name}")
//                Log.d(TAG, " fireurl :${fireurl}")

                val task = DownloadTask.Builder(fireurl, parentFile!!)
                    .setFilename(name)
                    .setMinIntervalMillisCallbackProcess(30)
                    .setPassIfAlreadyCompleted(false)
                    .build()
                tasklist.add(task)

            } while (cursor.moveToNext())
        }
        cursor.close()
        return tasklist

    }


}