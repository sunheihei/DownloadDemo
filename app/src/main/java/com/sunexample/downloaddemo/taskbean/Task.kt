package com.sunexample.downloaddemo.taskbean
import java.io.*

/**
 * iscompleted  0 =  false   1= true
 */


data class Task(
    val name: String,
    val url: String,
    val Thumbnail: String,
    var iscompleted: Int = 0,
    var tag: String = name + System.currentTimeMillis(),
    var currentOffset: Long = 0,
    var totalLength: Long = 0
) : Serializable