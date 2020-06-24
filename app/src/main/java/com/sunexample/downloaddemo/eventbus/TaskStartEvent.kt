package com.sunexample.downloaddemo.eventbus

import com.liulishuo.okdownload.DownloadTask

data class TaskStartEvent(val task: DownloadTask) {
}