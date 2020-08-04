package com.sunexample.downloaddemo.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.sunexample.downloaddemo.Const
import com.sunexample.downloaddemo.Const.TABLENAME

class TaskDatabaseHelper(val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {



    private val createTask = "create table ${TABLENAME} (" +
            " id integer primary key autoincrement," +
            " ${Const.NAME} text," +
            " ${Const.URL} text," +
            " ${Const.TASKTAG} text," +
            " ${Const.THUMBNAIL} text," +
            " ${Const.ISCOMPLETED} Int," +
            " ${Const.CURRENTOFFSET} Long," +
            " ${Const.TOTALLENGTH} Long )"

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(createTask)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }


}