package com.sunexample.downloaddemo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import android.util.Log
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher
import com.sunexample.downloaddemo.Const.DBNAME
import com.sunexample.downloaddemo.Const.TABLENAME
import com.sunexample.downloaddemo.sqlite.TaskDatabaseHelper
import java.io.File

object DownloadTaskManager {

    var db: SQLiteDatabase? = null

    var parentFile: File? = null

    fun initManager(context: Context) {
        parentFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val dbhelper = TaskDatabaseHelper(context, DBNAME, 1)
        db = dbhelper.writableDatabase
        initTaskQueue()
    }

    private fun initTaskQueue() {
        DownloadTaskQueue.addAll(getDataFromDatabase())
        Log.d(TAG, " DownloadTaskQueue.size : ${DownloadTaskQueue.size}")
    }

    private fun getDataFromDatabase(): List<DownloadTask> {
        val tasklist = mutableListOf<DownloadTask>()
        val cursor = db?.query(TABLENAME, null, null, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex("taskname"))
                val fireurl = cursor.getString(cursor.getColumnIndex("fileurl"))
                Log.d(TAG, " name : ${name}")
                Log.d(TAG, " fireurl :${fireurl}")

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

    var DownloadTaskQueue = mutableListOf<DownloadTask>()

}