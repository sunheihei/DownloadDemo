package com.sunexample.downloaddemo.eventbus

import com.liulishuo.okdownload.DownloadTask

data class TaskProgressEvent(val task: DownloadTask, val currentOffset: Long, val totalLength: Long) {
}