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

package com.dugang.rely.common.extension

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.net.Uri
import android.os.Build
import java.io.File
import java.util.*


/**
 * 获取应用的进程id
 */
val MY_PID: Int = android.os.Process.myPid()

/**
 * 获取唯一的UUID
 */
val MY_UUID: String = UUID.randomUUID().toString()



/**
 * 获取App版本名称
 */
val Context.versionName: String
    get() = packageManager.getPackageInfo(packageName, 0).versionName

/**
 * 获取App内部版本号
 */
val Context.versionCode: Long
    @TargetApi(28)
    get() = packageManager.getPackageInfo(packageName, 0).longVersionCode

/**
 * 判断app是否在前台运行
 */
fun Context.isForeground(): Boolean {
    val appProcesses = activityManager.runningAppProcesses
    return appProcesses.any { it.processName == packageName }
}

/**
 * 获取应用MD5签名
 */
fun Context.getSignatureMD5(format: Boolean = true): String {
    val signature = getSignature()[0].toByteArray().md5()
    return if (format) {
        val sb = StringBuilder()
        for (i in 0 until signature.length step 2) sb.append(signature.substring(i, i + 2)).append(":")
        sb.deleteCharAt(sb.length - 1).toString().toUpperCase()
    } else
        signature
}

/**
 * 获取应用SHA1签名
 */
fun Context.getSignatureSHA1(format: Boolean = true): String {
    val signature = getSignature()[0].toByteArray().sha1()
    return if (format) {
        val sb = StringBuilder()
        for (i in 0 until signature.length step 2) sb.append(signature.substring(i, i + 2)).append(":")
        sb.deleteCharAt(sb.length - 1).toString().toUpperCase()
    } else
        signature
}

/**
 * 获取应用SHA256签名
 */
fun Context.getSignatureSHA256(format: Boolean = true): String {
    val signature = getSignature()[0].toByteArray().sha256()
    return if (format) {
        val sb = StringBuilder()
        for (i in 0 until signature.length step 2) sb.append(signature.substring(i, i + 2)).append(":")
        sb.deleteCharAt(sb.length - 1).toString().toUpperCase()
    } else
        signature
}

@TargetApi(Build.VERSION_CODES.P)
private fun Context.getSignature(): Array<Signature> =
        packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo.apkContentsSigners


/**
 * 安装app,需要权限android.permission.REQUEST_INSTALL_PACKAGES
 */
fun Context.installApp(file: File) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        setDataAndType(file.getUri(this@installApp), "application/vnd.android.package-archive")
    }
    startActivity(intent)
}

/**
 * 卸载app,需要权限android.permission.DELETE_PACKAGES
 */
fun Context.uninstallApp() {
    val intent = Intent(Intent.ACTION_DELETE).apply {
        data = Uri.parse("package:$packageName")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}

/**
 * 重新启动app
 */
fun Context.relaunchApp() {
    val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return
    startActivity(Intent.makeRestartActivityTask(intent.component))
    System.exit(0)
}

/**
 * 彻底退出App
 */
fun closeApp() {
    android.os.Process.killProcess(MY_PID)
}