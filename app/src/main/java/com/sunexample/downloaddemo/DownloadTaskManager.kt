package com.sunexample.downloaddemo

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import android.util.Log
import com.liulishuo.okdownload.DownloadTask
import com.sunexample.downloaddemo.Const.CURRENTOFFSET
import com.sunexample.downloaddemo.Const.DBNAME
import com.sunexample.downloaddemo.Const.ISCOMPLETED
import com.sunexample.downloaddemo.Const.NAME
import com.sunexample.downloaddemo.Const.URL
import com.sunexample.downloaddemo.Const.TABLENAME
import com.sunexample.downloaddemo.Const.TAG_TASK
import com.sunexample.downloaddemo.Const.TASKTAG
import com.sunexample.downloaddemo.Const.TASK_TAG_KEY
import com.sunexample.downloaddemo.Const.THUMBNAIL
import com.sunexample.downloaddemo.Const.TOTALLENGTH
import com.sunexample.downloaddemo.taskbean.Task
import com.sunexample.downloaddemo.sqlite.TaskDatabaseHelper
import java.io.File

object DownloadTaskManager {

    private var db: SQLiteDatabase? = null

    private var parentFile: File? = null

    //用于okdonwload的下载任务队列
    var DownloadTaskQueue = mutableListOf<DownloadTask>()

    //自己封装的下载任务队列，用于列表展示
    var CusTomTaskQueue = mutableListOf<Task>()

    //已经下载完成的任务队列
    var DownloadedTaskQueue = mutableListOf<Task>()

    fun initManager(context: Context) {
        //确定路径
        parentFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val dbhelper = TaskDatabaseHelper(context, DBNAME, 1)
        db = dbhelper.writableDatabase
        initTaskQueueWhenLaunch()
    }


    //添加一个新任务
    fun startNewTask(context: Context, task: Task) {
        DownloadTaskQueue.forEach {
            if (it.filename == task.name && it.url == task.url) {
                Log.d(TAG, "The file is already in the download queue")
                return
            }
        }

        if (File(getParentFile(), task.name).exists()) {
            Log.d(TAG, "The file already exists")
            return
        }

        addTaskToDownloadQueue(task)

        context.startService(
            Intent(context, TaskService::class.java).setAction(Const.TAG_START_NEW_TASK)
                .putExtra(TAG_TASK, task)
        )
    }


    fun pauseAllTasks(context: Context) {
        context.startService(
            Intent(
                context,
                TaskService::class.java
            ).setAction(Const.TAG_STOP_ALL_TASK)
        )
    }

    fun startAllTasks(context: Context) {
        context.startService(
            Intent(
                context,
                TaskService::class.java
            ).setAction(Const.TAG_START_ALL_TASK)
        )
    }


    fun stopDownload(context: Context) {
        context.stopService(Intent(context, TaskService::class.java))
    }

    /**
     * 当某个任务下载完成时，同步三个队列数据，并更新数据库状态
     */
    fun synchronizeTask(position: Int) {
        //更新该任务状态
        updataTaskStatus(CusTomTaskQueue[position])//更新是否已经下载完成
        synchronizeProgrss(position)//下载完的任务将进度更新到表中
        DownloadedTaskQueue.add(CusTomTaskQueue[position])
        DownloadTaskQueue.removeAt(position)
        CusTomTaskQueue.removeAt(position)
    }

    /**
     * 更新数据库任务进度
     */
    fun synchronizeProgrss(position: Int) {
        if (CusTomTaskQueue.size != 0)
            updataProgress(CusTomTaskQueue[position])
    }


    /**
     * 删除下载任务列表中某个任务，同步列表，并且删除文件和数据库信息
     */
    fun synchronizeWhenDelete(position: Int) {
        DownloadTaskQueue[position].cancel()
        deleteTaskFromDateBase(CusTomTaskQueue[position])
        if (DownloadTaskQueue[position].file!!.exists()) {
            DownloadTaskQueue[position].file!!.delete()
        }
        DownloadTaskQueue.removeAt(position)
        CusTomTaskQueue.removeAt(position)
    }


    /**
     *删除已完成中的某个任务
     */
    fun synchronizeWhenDeleteCompleted(position: Int) {
        deleteTaskFromDateBase(DownloadedTaskQueue[position])
        val tempFile = File(getParentFile(), DownloadedTaskQueue[position].name)
        if (tempFile.exists()) {
            tempFile.delete()
        }
        DownloadedTaskQueue.removeAt(position)
    }


    /**
     * //启动时从数据库中取出上一次没下载完的所有任务
     */
    private fun initTaskQueueWhenLaunch() {
        DownloadedTaskQueue.clear()
        DownloadTaskQueue.clear()
        CusTomTaskQueue.clear()
        getDataFromDatabase()
        Log.d(TAG, " DownloadTaskQueue.size : ${DownloadTaskQueue.size}")
        Log.d(TAG, " CusTomTaskQueue.size : ${CusTomTaskQueue.size}")
        Log.d(TAG, " DownloadedTaskQueue.size : ${DownloadedTaskQueue.size}")
    }


    //新添加一条任务，分别是下载任务列表
    fun addTaskToDownloadQueue(task: Any) {
        if (task is Task) {
            CusTomTaskQueue.add(task)
            addTaskToDataBase(task)
        } else if (task is DownloadTask) {
            DownloadTaskQueue.add(task)
        }
//        Log.d(TAG, " DownloadTaskQueue.size : ${DownloadTaskQueue.size}")
//        Log.d(TAG, " CusTomTaskQueue.size : ${CusTomTaskQueue.size}")
    }


    /**
     * 插入一条任务到数据库
     */
    private fun addTaskToDataBase(task: Task) {
        val value = ContentValues().apply {
            put(NAME, task.name)
            put(URL, task.url)
            put(TASKTAG, task.tag)
            put(ISCOMPLETED, task.isCompleted)
            put(THUMBNAIL, task.thumbnail)
            put(CURRENTOFFSET, task.currentOffset)
            put(TOTALLENGTH, task.totalLength)
        }
        db!!.insert(TABLENAME, null, value)
    }

    /**
     *删除一条任务
     */
    private fun deleteTaskFromDateBase(task: Task) {
        db!!.delete(TABLENAME, "$TASKTAG  =  ?", arrayOf(task.tag))
    }

    /*
     * 更新任务下载状态
     */
    private fun updataTaskStatus(task: Task) {
        val value = ContentValues().apply {
            put(ISCOMPLETED, 1)
        }
        db!!.update(TABLENAME, value, "$TASKTAG  =  ?", arrayOf(task.tag))
    }

    /**
     * 暂停任务时更新任务进度
     */
    private fun updataProgress(task: Task) {
        val value = ContentValues().apply {
            put(CURRENTOFFSET, task.currentOffset)
            put(TOTALLENGTH, task.totalLength)
        }
        db!!.update(TABLENAME, value, "$TASKTAG  =  ?", arrayOf(task.tag))
    }


    /**
     * 从数据库查找全部任务，分别放进任务队列
     */
    @SuppressLint("Range")
    private fun getDataFromDatabase() {
        val cursor = db?.query(TABLENAME, null, null, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(NAME))
                val url = cursor.getString(cursor.getColumnIndex(URL))
                val taskTag = cursor.getString(cursor.getColumnIndex(TASKTAG))
                val isCompleted = cursor.getInt(cursor.getColumnIndex(ISCOMPLETED))
                val thumbnail = cursor.getString(cursor.getColumnIndex(THUMBNAIL))
                val currentOffset = cursor.getLong(cursor.getColumnIndex(CURRENTOFFSET))
                val totalLength = cursor.getLong(cursor.getColumnIndex(TOTALLENGTH))


//                Log.d(TAG, " name : ${name}")
//                Log.d(TAG, " fireurl :${url}")
//                Log.d(TAG, " task_tag :${task_tag}")
//                Log.d(TAG, " thombnail :${thombnail}")
//                Log.d(TAG, " iscompleted :${iscompleted}")
//                Log.d(TAG, " currentoffset :${currentoffset}")
//                Log.d(TAG, " totalLength :${totalLength}")

                val task = Task(
                    name,
                    url,
                    thumbnail,
                    isCompleted,
                    taskTag,
                    currentOffset,
                    totalLength
                )

                if (isCompleted == 1) {
                    //已经完成
                    if (File(getParentFile(), name).exists()) {
                        //且该文件存在
                        DownloadedTaskQueue.add(task)
                    } else {
                        //数据库下载完成，但是文件不存在，那么要删除该数据
                        deleteTaskFromDateBase(task)
                    }
                } else {
                    val downLoadTask = DownloadTask.Builder(url, getParentFile())
                        .setFilename(name)
                        .setConnectionCount(1)
                        .setMinIntervalMillisCallbackProcess(1000)
                        .setPassIfAlreadyCompleted(false)
                        .build()
                    downLoadTask.addTag(TASK_TAG_KEY, taskTag)

                    DownloadTaskQueue.add(downLoadTask)

                    CusTomTaskQueue.add(task)
                }


            } while (cursor.moveToNext())
        }
        cursor.close()

    }


    fun getParentFile(): File = parentFile!!

}