package com.sunexample.downloaddemo.TaskBean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * iscompleted  0 =  false   1= true
 */

@Parcelize
data class Task(
    val name: String,
    val url: String,
    val Thumbnail: String,
    var iscompleted: Int = 0,
    var tag: String = name + System.currentTimeMillis(),
    var currentOffset: Long = 0,
    var totalLength: Long = 0
) : Parcelable