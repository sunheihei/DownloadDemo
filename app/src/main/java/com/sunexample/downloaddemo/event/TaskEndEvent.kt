package com.sunexample.downloaddemo.event

import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import java.lang.Exception

data class TaskEndEvent(val task: DownloadTask, val cause: EndCause, val realCause: Exception?) {
}