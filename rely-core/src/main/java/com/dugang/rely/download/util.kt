/*
 *    Copyright (c) 2017-2019 dugang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.dugang.rely.download

import java.io.File

/**
 * Created by dugang on 2018/11/16.
 */

data class DownloadInfo(
        var id: Long = 0,
        var url: String = "",
        var savePath: String = "",
        var total: Long = 0,
        var progress: Long = 0,
        var status: Int = DownloadManager.STATUS_PENDING
)

/**
 * DownloadInfo的深度copy
 */
fun DownloadInfo.deepCopy(): DownloadInfo = DownloadInfo().apply {
    id = this@deepCopy.id
    url = this@deepCopy.url
    savePath = this@deepCopy.savePath
    total = this@deepCopy.total
    progress = this@deepCopy.progress
    status = this@deepCopy.status
}

/**
 * 下载监听器
 */
interface OnDownloadListener {
    fun onPending()

    fun onProgress(progress: Long, total: Long)

    fun onCompleted(file: File)

    fun onPaused()

    fun onCanceled()

    fun onFailed()
}