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

package com.dugang.rely.upload

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import com.dugang.rely.common.extension.closeIO
import com.dugang.rely.common.extension.currentTime
import com.dugang.rely.common.extension.isNotNull
import okhttp3.*
import okhttp3.Headers.Companion.headersOf
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink
import okio.Okio
import okio.Source
import okio.source
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Created by dugang on 2018/11/26. 批量上传
 */
@Suppress("unused")
class UploadManager(private val url: String, private val interceptor: Interceptor? = null, private val maxThread: Int = 3) {
    companion object {
        const val STATUS_PENDING = 0
        const val STATUS_RUNNING = 1
        const val STATUS_COMPLETED = 2
        const val STATUS_FAILED = 3
        const val STATUS_CANCELED = 4
    }

    private val uploadExecutor: ExecutorService by lazy { Executors.newFixedThreadPool(maxThread) }
    private val futureMap: ConcurrentHashMap<Long, Future<*>>   by lazy { ConcurrentHashMap<Long, Future<*>>() }

    private val taskMap: ConcurrentHashMap<Long, UploadTask>   by lazy { ConcurrentHashMap<Long, UploadTask>() }
    private val listenerMap: ConcurrentHashMap<Long, OnUploadListener> by lazy { ConcurrentHashMap<Long, OnUploadListener>() }
    private val mHandler: ExecutorHandler by lazy { ExecutorHandler() }


    fun addTask(file: File, alias: String = "file"): Long {
        val uploadInfo = UploadInfo().apply {
            this.id = file.absolutePath.hashCode().toLong()
            this.path = file.absolutePath
            this.alias = alias
            this.total = file.length()
        }
        if (!taskMap.containsKey(uploadInfo.id)) taskMap[uploadInfo.id] = UploadTask(uploadInfo)
        return uploadInfo.id
    }


    /**
     * 开始单个任务
     */
    fun start(uploadId: Long) {
        taskMap[uploadId]?.let {
            if (it.uploadInfo.status == STATUS_PENDING && !futureMap.containsKey(uploadId) || it.uploadInfo.status == STATUS_FAILED) {
                it.uploadInfo.status = STATUS_PENDING
                futureMap[it.uploadInfo.id] = uploadExecutor.submit(it)
                mHandler.sendMessage(Message().apply { obj = it.uploadInfo })
            }
        }
    }

    /**
     * 开始全部任务
     */
    fun startAll() {
        taskMap.values.forEach { start(it.uploadInfo.id) }
    }


    /**
     * 取消正在上传或等待上传的任务
     */
    fun cancel(uploadId: Long) {
        taskMap[uploadId]?.let {
            if (it.uploadInfo.status == STATUS_RUNNING)
                it.uploadInfo.status = STATUS_CANCELED
            else {
                futureMap[uploadId]?.cancel(true)
                mHandler.sendMessage(Message().apply { obj = it.uploadInfo.apply { status =
                    STATUS_CANCELED
                } })
            }
        }
    }

    /**
     * 取消全部任务
     */
    fun cancelALl() {
        taskMap.values.forEach { cancel(it.uploadInfo.id) }
    }

    /**
     * 注册监听器
     * @param uploadId 上传id,在addTask()时返回
     * @param onUploadListener 观察者
     */
    fun registerListener(uploadId: Long, onUploadListener: OnUploadListener) {
        if (!listenerMap.containsKey(uploadId)) listenerMap[uploadId] = onUploadListener
    }

    /**
     * 清除监听器
     */
    fun unregisterListener(uploadId: Long? = null) {
        if (uploadId.isNotNull())
            listenerMap.clear()
        else
            listenerMap.remove(uploadId)
    }

    @SuppressLint("HandlerLeak")
    private inner class ExecutorHandler : Handler() {
        override fun handleMessage(msg: Message) {
            val uploadInfo = msg.obj as UploadInfo
            when (uploadInfo.status) {
                STATUS_PENDING -> {
                    listenerMap[uploadInfo.id]?.onPending()
                }
                STATUS_RUNNING -> {
                    listenerMap[uploadInfo.id]?.onUpload(uploadInfo.progress, uploadInfo.total)
                }
                STATUS_COMPLETED -> {
                    taskMap.remove(uploadInfo.id)
                    futureMap.remove(uploadInfo.id)
                    listenerMap[uploadInfo.id]?.onCompleted(uploadInfo.response)
                }
                STATUS_CANCELED -> {
                    taskMap.remove(uploadInfo.id)
                    futureMap.remove(uploadInfo.id)

                    listenerMap[uploadInfo.id]?.onCanceled()
                }
                STATUS_FAILED -> {
                    futureMap.remove(uploadInfo.id)
                    listenerMap[uploadInfo.id]?.onFailed()
                }
            }
        }
    }

    private inner class UploadTask(var uploadInfo: UploadInfo) : Runnable {
        private val file: File by lazy { File(uploadInfo.path) }
        private var milliseconds: Long = 0

        private fun sendMessage(uploadInfo: UploadInfo) {
            if (uploadInfo.status == STATUS_RUNNING) {
                if (uploadInfo.progress == uploadInfo.total)
                    mHandler.sendMessage(Message().apply { obj = uploadInfo })
                else if (currentTime - milliseconds > 500) {
                    mHandler.sendMessage(Message().apply { obj = uploadInfo })
                    milliseconds = currentTime
                }
            } else {
                mHandler.sendMessage(Message().apply { obj = uploadInfo })
            }
        }

        override fun run() {
            try {
                val formData = "form-data;name=${uploadInfo.alias}; filename=${file.name}"
                val multipartBody = MultipartBody.Builder().apply {
                    addPart(headersOf("Content-Disposition", formData), UploadRequestBody())
                    setType(MultipartBody.FORM)
                }.build()
                val client = OkHttpClient.Builder().apply { interceptor?.let { addInterceptor(it) } }.build()
                val response = client.newCall(Request.Builder().url(url).post(multipartBody).build()).execute()
                uploadInfo.apply {
                    this.response = response.body?.string()
                    this.status = if (response.isSuccessful) STATUS_COMPLETED else STATUS_FAILED
                }
                sendMessage(uploadInfo.deepCopy())
            } catch (e: Exception) {
                sendMessage(uploadInfo.apply { if (status != STATUS_CANCELED) status =
                    STATUS_FAILED
                }.deepCopy())
            }
        }

        inner class UploadRequestBody : RequestBody() {
            override fun contentLength(): Long = file.length()

            override fun contentType(): MediaType? = "application/octet-stream".toMediaTypeOrNull()

            override fun writeTo(sink: BufferedSink) {
                var source: Source? = null
                try {
                    source = file.source()
                    uploadInfo.status = STATUS_RUNNING
                    var progress: Long = 0
                    var read: Long
                    do {
                        if (uploadInfo.status == STATUS_CANCELED) break
                        read = source.read(sink.buffer, 2048)
                        if (read == -1L) break
                        progress += read
                        sink.flush()
                        sendMessage(uploadInfo.apply { this.progress = progress }.deepCopy())
                    } while (true)
                } catch (e: Exception) {
                    sendMessage(uploadInfo.apply { status =
                        STATUS_FAILED
                    }.deepCopy())
                } finally {
                    closeIO(source)
                }
            }
        }
    }
}