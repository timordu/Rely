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

package com.android.rely.download

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import com.android.rely.Rely
import com.android.rely.common.*
import com.android.rely.ext.wifiConnected
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Created by dugang on 2018/11/14. 批量下载
 */
@Suppress("unused")
class DownloadManager(private val rootPath: String = PUBLIC_DOWNLOAD_DIR, private val maxThread: Int = 3, private val limitWiFi: Boolean = true) {
    companion object {
        const val STATUS_PENDING = 0
        const val STATUS_RUNNING = 1
        const val STATUS_PAUSED = 2
        const val STATUS_COMPLETED = 3
        const val STATUS_FAILED = 4
        const val STATUS_CANCELED = 5
    }


    private val downLoadExecutor: ExecutorService by lazy { Executors.newFixedThreadPool(maxThread) }
    private val futureMap: ConcurrentHashMap<Long, Future<*>>   by lazy { ConcurrentHashMap<Long, Future<*>>() }

    private val taskMap: ConcurrentHashMap<Long, DownloadTask>   by lazy { ConcurrentHashMap<Long, DownloadTask>() }
    private val listenerMap: ConcurrentHashMap<Long, OnDownloadListener> by lazy { ConcurrentHashMap<Long, OnDownloadListener>() }
    private val mHandler: ExecutorHandler by lazy { ExecutorHandler() }


    fun addTask(url: String): Long {
        if (url.isEmpty() || url.isBlank() || !url.contains("/")) return 0

        //处理私有对象存储中包含token的情况
        val realUrl = if (url.contains("?")) url.substring(0, url.indexOf("?")) else url
        val fileName = realUrl.substring(realUrl.lastIndexOf("/") + 1, realUrl.length)
        val downloadInfo = DownloadInfo().apply {
            id = url.hashCode().toLong()
            this.url = url
            savePath = File(rootPath, fileName).absolutePath
        }
        val downloadTask = DownloadTask(downloadInfo)

        if (!taskMap.containsKey(downloadInfo.id)) taskMap[downloadInfo.id] = downloadTask

        submitTask(downloadTask)
        return downloadInfo.id
    }

    private fun submitTask(downloadTask: DownloadTask) {
        if (limitWiFi) {
            if (Rely.appContext.wifiConnected)
                futureMap[downloadTask.downloadInfo.id] = downLoadExecutor.submit(downloadTask)
        } else
            futureMap[downloadTask.downloadInfo.id] = downLoadExecutor.submit(downloadTask)

        mHandler.sendMessage(Message().apply { obj = downloadTask.downloadInfo })
    }


    /**
     * 开始单个任务,限制:暂停或失败状态
     */
    fun start(downloadId: Long) {
        taskMap[downloadId]?.let {
            if (it.downloadInfo.status == STATUS_PENDING && !futureMap.containsKey(downloadId)) {
                submitTask(it)
            }

            if (it.downloadInfo.status == STATUS_PAUSED || it.downloadInfo.status == STATUS_FAILED) {
                it.downloadInfo.status = STATUS_PENDING
                submitTask(it)
            }
        }
    }

    /**
     * 开始全部任务
     */
    fun startAll() {
        taskMap.values.forEach { start(it.downloadInfo.id) }
    }

    /**
     * 暂停单个任务,只能暂停等待中或下载中的任务
     */
    fun pause(downloadId: Long) {
        taskMap[downloadId]?.let {
            //先暂停正在下载的
            if (it.downloadInfo.status == STATUS_RUNNING) it.downloadInfo.status = STATUS_PAUSED
            //然后再暂停等待下载的
            if (it.downloadInfo.status == STATUS_PENDING) {
                futureMap[downloadId]?.cancel(true)
                mHandler.sendMessage(Message().apply { obj = it.downloadInfo.apply { status = STATUS_PAUSED } })
            }
        }
    }

    /**
     * 暂停全部任务
     */
    fun pauseAll() {
        taskMap.values.forEach { pause(it.downloadInfo.id) }
    }

    /**
     * 取消任务,删除未完成的下载文件
     */
    fun cancel(downloadId: Long) {
        taskMap[downloadId]?.let {
            if (it.downloadInfo.status == STATUS_RUNNING)
                it.downloadInfo.status = STATUS_CANCELED
            else {
                futureMap[downloadId]?.cancel(true)
                mHandler.sendMessage(Message().apply { obj = it.downloadInfo.apply { status = STATUS_CANCELED } })
            }
        }
    }

    /**
     * 取消全部任务
     */
    fun cancelAll() {
        taskMap.values.forEach { cancel(it.downloadInfo.id) }
    }

    /**
     * 注册监听器
     * @param downloadId 下载id,在addTask()时返回
     * @param onDownloadListener 观察者
     */
    fun registerListener(downloadId: Long, onDownloadListener: OnDownloadListener) {
        if (!listenerMap.containsKey(downloadId)) listenerMap[downloadId] = onDownloadListener
    }

    /**
     * 清除监听器
     */
    fun unregisterListener(downloadId: Long? = null) {
        if (downloadId.isNotNull())
            listenerMap.clear()
        else
            listenerMap.remove(downloadId)

    }

    /**
     * 执行消息分发
     */
    @SuppressLint("HandlerLeak")
    private inner class ExecutorHandler : Handler() {
        override fun handleMessage(msg: Message) {
            val downloadInfo = msg.obj as DownloadInfo
            when (downloadInfo.status) {
                //状态-等待
                STATUS_PENDING -> {
                    listenerMap[downloadInfo.id]?.onPending()
                }
                //状态-下载中
                STATUS_RUNNING -> {
                    listenerMap[downloadInfo.id]?.onProgress(downloadInfo.progress, downloadInfo.total)
                }
                //状态-暂停
                STATUS_PAUSED -> {
                    futureMap.remove(downloadInfo.id)
                    listenerMap[downloadInfo.id]?.onPaused()
                }
                //状态-取消
                STATUS_CANCELED -> {
                    taskMap.remove(downloadInfo.id)
                    futureMap.remove(downloadInfo.id)

                    if (downloadInfo.status != STATUS_COMPLETED) File(downloadInfo.savePath).delete()
                    listenerMap[downloadInfo.id]?.onCanceled()
                }
                //状态-下载完成
                STATUS_COMPLETED -> {
                    taskMap.remove(downloadInfo.id)
                    futureMap.remove(downloadInfo.id)
                    listenerMap[downloadInfo.id]?.onCompleted(File(downloadInfo.savePath))
                }
                //状态-下载失败
                STATUS_FAILED -> {
                    futureMap.remove(downloadInfo.id)
                    listenerMap[downloadInfo.id]?.onFailed()
                }
            }
        }
    }

    private inner class DownloadTask(var downloadInfo: DownloadInfo) : Runnable {
        private val mClient: OkHttpClient by lazy { OkHttpClient.Builder().build() }
        private var milliseconds: Long = 0

        private fun sendMessage(downloadInfo: DownloadInfo) {
            if (downloadInfo.status == STATUS_RUNNING) {
                //下载完成前的消息必须发出
                if (downloadInfo.progress == downloadInfo.total)
                    mHandler.sendMessage(Message().apply { obj = downloadInfo })
                //下载中的消息,每隔500毫秒发送一次
                else if (currentTime - milliseconds > 500) {
                    mHandler.sendMessage(Message().apply { obj = downloadInfo })
                    milliseconds = currentTime
                }
            } else {
                mHandler.sendMessage(Message().apply { obj = downloadInfo })
            }
        }

        override fun run() {
            var inputStream: InputStream? = null
            var saveFile: RandomAccessFile? = null
            //获取文件长度
            try {
                val lenResponse = mClient.newCall(Request.Builder().url(downloadInfo.url).build()).execute()
                if (!lenResponse.isSuccessful || lenResponse.body() == null) throw Exception("文件长度获取失败")
                downloadInfo.total = lenResponse.body()!!.contentLength()
                val file = File(downloadInfo.savePath)

                //开启断点续传
                val rang = "bytes=${file.length()}-${downloadInfo.total}"
                val request = Request.Builder().addHeader("RANGE", rang).url(downloadInfo.url).build()
                //开始文件下载
                mClient.newCall(request).execute().body()?.let { body ->
                    downloadInfo.status = STATUS_RUNNING
                    inputStream = body.byteStream()
                    saveFile = RandomAccessFile(file, "rw")
                    //跳过已经下载的字节
                    saveFile!!.seek(file.length())

                    val byteArray = ByteArray(2048)
                    var len: Int
                    do {
                        if (downloadInfo.status == STATUS_PAUSED || downloadInfo.status == STATUS_CANCELED) break
                        len = inputStream!!.read(byteArray)
                        if (len == -1) break
                        saveFile!!.write(byteArray, 0, len)
                        sendMessage(downloadInfo.apply { progress = file.length() }.deepCopy())
                    } while (true)
                    body.close()
                    sendMessage(downloadInfo.apply { status = STATUS_COMPLETED }.deepCopy())
                }
            } catch (e: Exception) {
                sendMessage(downloadInfo.apply { status = STATUS_FAILED }.deepCopy())
            } finally {
                closeIO(inputStream, saveFile)
            }
        }
    }
}