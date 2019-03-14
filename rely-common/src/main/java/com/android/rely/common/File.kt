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

@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.android.rely.common

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.core.content.FileProvider
import com.blankj.ALog
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

/**
 * 获取应用文件目录 ("/data/data/<包名>")
 */
val Context.DATA_DIR_PATH: String
    get() = filesDir.parent

/**
 * 获取应用文件目录 ("/data/data/<包名>/files")
 */
val Context.FILE_DIR_PATH: String
    get() = filesDir.absolutePath

/**
 * 获取应用缓存目录("/data/data/<包名>/cache")
 */
val Context.CACHE_DIR_PATH: String
    get() = cacheDir.absolutePath

/**
 * 获取应用外置文件目录("/Android/data/<包名>/files")
 */
val Context.EXTERNAL_FILE_DIR_PATH: String
    get() = getExternalFilesDir(null).absolutePath

/**
 * 获取应用外置缓存目录"/Android/data/<包名>/cache")
 */
val Context.EXTERNAL_CACHE_DIR_PATH: String
    get() = externalCacheDir.absolutePath

/**
 * 获取公共下载文件夹路径
 */
val PUBLIC_DOWNLOAD_DIR: String
    get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath

/**
 * 获取公共的照片文件夹路径
 */
val PUBLIC_DCIM_DIR: String
    get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath

/**
 * 获取公共的图片文件夹路径
 */
val PUBLIC_PICTURE_DIR: String
    get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath

/**
 * 获取公共的音乐文件夹路径
 */
val PUBLIC_MUSIC_DIR: String
    get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath

/**
 * 获取公共的电影文件夹路径
 */
val PUBLIC_MOVIE_DIR: String
    get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath

/**
 * 获取Sdcard的根目录
 */
val EXTERNAL_DIR_ROOT: String
    get() = Environment.getExternalStorageDirectory().absolutePath

/**
 * 内存卡是否挂载
 */
val isExternalStorageWritable: Boolean
    get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

/**
 * 获取内存卡空间总大小
 */
val EXTERNAL_STORAGE_SIZE: Long
    get() = if (isExternalStorageWritable) {
        val statFs = StatFs(Environment.getExternalStorageDirectory().path)
        statFs.blockSizeLong * statFs.blockCountLong
    } else
        0L

/**
 * 获取内存卡空间可用大小
 */
val EXTERNAL_STORAGE_AVAILABLE_SIZE: Long
    get() = if (isExternalStorageWritable) {
        val statFs = StatFs(Environment.getExternalStorageDirectory().path)
        statFs.blockSizeLong * statFs.availableBlocksLong
    } else 0L

/**
 * 判断是否是文件路径
 */
fun String.isFilePath(): Boolean = File(this).isFile

/**
 * 判断是否是文件夹路径
 */
fun String.isFolderPath(): Boolean = File(this).isDirectory

/**
 * 判断该路径的文件或文件夹是否存在
 */
fun String.fileExist(): Boolean = File(this).exists()

/**
 * 获取文件的Uri
 */
fun File.getUri(context: Context, authority: String = context.packageName + ".fileProvider"): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(context, authority, this)
    } else {
        Uri.fromFile(this)
    }
}

/**
 * 创建目录. eg. /path or /path/path
 */
fun String.createFolder(): String =
    File(EXTERNAL_DIR_ROOT, this).apply {
        if (!exists()) mkdirs()
    }.absolutePath

/**
 * 根据路径创建文件的父级目录,并返回该路径的绝对路径
 */
fun String.createParentFile(): String =
    File(EXTERNAL_DIR_ROOT, this).apply {
        if (!parentFile.exists()) parentFile.mkdirs()
    }.absolutePath


/**
 * 清空文件夹
 */
fun File.clearFolder() {
    if (exists() && isDirectory) {
        listFiles().forEach {
            if (it.isFile) it.delete() else it.clearFolder()
        }
        delete()
    }
}

/**
 * 获取文件夹的大小
 */
fun File.getFolderSize(): Long {
    var size = 0L
    if (exists() && isDirectory) {
        listFiles().forEach {
            if (it.isFile)
                size += it.length()
            else
                it.getFolderSize()
        }
    }
    return size
}


/**
 * 获取文件的Md5
 */
fun File.md5(): String {
    val fis = FileInputStream(this)
    val md = MessageDigest.getInstance("MD5")
    val dataBytes = ByteArray(1024)
    var read: Int
    do {
        read = fis.read(dataBytes)
        if (read != -1) md.update(dataBytes, 0, read) else break
    } while (true)
    return md.digest().toHex()
}