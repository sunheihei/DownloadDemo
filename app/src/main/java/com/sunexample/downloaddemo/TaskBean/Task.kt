package com.sunexample.downloaddemo.TaskBean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(
    val name: String,
    val url: String,
    val Thumbnail: String,
    var CurrentOffset: Long,
    var TotalLength: Long
) : Parcelable