package com.sunexample.downloaddemo.event

import com.liulishuo.okdownload.DownloadTask

data class TaskProgressEvent(val task: DownloadTask, val currentOffset: Long, val totalLength: Long) {
}