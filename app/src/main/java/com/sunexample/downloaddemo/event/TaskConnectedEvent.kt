package com.sunexample.downloaddemo.event

import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import java.lang.Exception

data class TaskConnectedEvent(
    val task: DownloadTask,
    val blockCount: Int,
    val currentOffset: Long,
    val totalLength: Long
) {
}