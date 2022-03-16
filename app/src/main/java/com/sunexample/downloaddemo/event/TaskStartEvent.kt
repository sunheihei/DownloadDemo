package com.sunexample.downloaddemo.event

import com.liulishuo.okdownload.DownloadTask

data class TaskStartEvent(val task: DownloadTask) {
}