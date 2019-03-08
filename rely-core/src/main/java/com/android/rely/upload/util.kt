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

package com.android.rely.upload


/**
 * Created by dugang on 2018/11/26.
 */

data class UploadInfo(
        var id: Long = 0,
        var path: String = "",
        var alias: String = "file",
        var total: Long = 0,
        var progress: Long = 0,
        var status: Int = UploadManager.STATUS_PENDING,
        var response: String? = null)

fun UploadInfo.deepCopy(): UploadInfo = UploadInfo().apply {
    id = this@deepCopy.id
    path = this@deepCopy.path
    alias = this@deepCopy.alias
    total = this@deepCopy.total
    progress = this@deepCopy.progress
    status = this@deepCopy.status
    response = this@deepCopy.response
}


interface OnUploadListener {
    fun onPending()

    fun onUpload(progress: Long, total: Long)

    fun onCompleted(response: String?)

    fun onCanceled()

    fun onFailed()
}


