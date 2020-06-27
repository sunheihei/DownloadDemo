package com.sunexample.downloaddemo

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import android.util.Log
import com.liulishuo.okdownload.DownloadTask
import com.sunexample.downloaddemo.Const.CURRENTOFFSET
import com.sunexample.downloaddemo.Const.DBNAME
import com.sunexample.downloaddemo.Const.NAME
import com.sunexample.downloaddemo.Const.URL
import com.sunexample.downloaddemo.Const.TABLENAME
import com.sunexample.downloaddemo.Const.THUMBNAIL
import com.sunexample.downloaddemo.Const.TOTALLENGTH
import com.sunexample.downloaddemo.TaskBean.Task
import com.sunexample.downloaddemo.sqlite.TaskDatabaseHelper
import java.io.File

object DownloadTaskManager {

    private var db: SQLiteDatabase? = null

    private var parentFile: File? = null

    //用于okdonwload的下载任务队列
    var DownloadTaskQueue = mutableListOf<DownloadTask>()

    //自己封装的下载任务队列，用于列表展示
    var CusTomTaskQueue = mutableListOf<Task>()

    fun initManager(context: Context) {
        //确定路劲
        parentFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val dbhelper = TaskDatabaseHelper(context, DBNAME, 1)
        db = dbhelper.writableDatabase
        initTaskQueueWhenLaunch()
    }


    fun StartTask(context: Context,task: Task) {

        DownloadTaskQueue.forEach {
            if (it.filename == task.name && it.url == task.url){
                return
            }
        }

        addTaskToDownloadQueue(task)

        val i = Intent(context, TaskService::class.java)
        i.putExtra(NAME, task.name)
        i.putExtra(URL, task.url)
        context.startService(i)
    }


    /**
     * //启动时从数据库中取出上一次没下载完的所有任务
     */
    private fun initTaskQueueWhenLaunch() {
        getDataFromDatabase()
        Log.d(TAG, " DownloadTaskQueue.size : ${DownloadTaskQueue.size}")
        Log.d(TAG, " CusTomTaskQueue.size : ${CusTomTaskQueue.size}")
    }


     //新添加一条任务，分别是下载任务列表
     fun addTaskToDownloadQueue(task: Any) {
         if (task is Task){
             CusTomTaskQueue.add(task)
             addTaskToDataBase(task)
         }else if(task is DownloadTask){
             DownloadTaskQueue.add(task)
         }
         Log.d(TAG, " DownloadTaskQueue.size : ${DownloadTaskQueue.size}")
         Log.d(TAG, " CusTomTaskQueue.size : ${CusTomTaskQueue.size}")
    }


    /**
     * 插入一条任务到数据库
     */
    private fun addTaskToDataBase(task: Task) {
        val value = ContentValues().apply {
            put(Const.NAME, task.name)
            put(Const.URL, task.url)
            put(Const.THUMBNAIL,task.url)
            put(Const.CURRENTOFFSET,task.currentOffset)
            put(Const.TOTALLENGTH,task.totalLength)
        }
        db!!.insert(Const.TABLENAME, null, value)
    }

    /**
     *删除一条任务
     */
    private fun deleteTaskFromDateBase(task: DownloadTask) {
        db!!.delete(Const.TABLENAME, "$NAME  = ", arrayOf(task.filename))
    }


    /**
     * 从数据库查找全部任务，分别放进任务队列
     */
   private fun getDataFromDatabase(){
        val cursor = db?.query(TABLENAME, null, null, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(NAME))
                val url = cursor.getString(cursor.getColumnIndex(URL))
                val thombnail= cursor.getString(cursor.getColumnIndex(THUMBNAIL))
                val currentoffset = cursor.getLong(cursor.getColumnIndex(CURRENTOFFSET))
                val totalLength = cursor.getLong(cursor.getColumnIndex(TOTALLENGTH))

//                Log.d(TAG, " name : ${name}")
//                Log.d(TAG, " fireurl :${url}")
//                Log.d(TAG, " thombnail :${thombnail}")
//                Log.d(TAG, " currentoffset :${currentoffset}")
//                Log.d(TAG, " totalLength :${totalLength}")

                val task = DownloadTask.Builder(url, parentFile!!)
                    .setFilename(name)
                    .setMinIntervalMillisCallbackProcess(30)
                    .setPassIfAlreadyCompleted(false)
                    .build()
                DownloadTaskQueue.add(task)

                CusTomTaskQueue.add(Task(name,url,thombnail,currentoffset,totalLength))


            } while (cursor.moveToNext())
        }
        cursor.close()

    }


    fun getParentFile():File = parentFile!!

}