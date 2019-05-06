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

package com.android.rely.common

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.webkit.WebSettings
import java.net.NetworkInterface
import java.util.*


/**
 * Created by dugang on 2018/12/7.
 */

/**
 * 获取系统的SDK名称
 */
val MY_SDK_VERION_NAME: String = Build.VERSION.RELEASE

/**
 * 获取系统的SDK版本
 */
val MY_SDK_VERSION_CODE: Int = Build.VERSION.SDK_INT

/**
 * 获取设备名称
 */
val MY_DEVICE_NAME: String = Build.MANUFACTURER + " " + Build.MODEL

/**
 * 获取设备ABI列表
 */
val MY_DEVICE_ABIS: Array<String> = Build.SUPPORTED_ABIS

/**
 * 获取Http的UserAgent
 */
fun Context.getUserAgent(): String = WebSettings.getDefaultUserAgent(this)

/**
 * 获取手机IMEI
 */
fun Context.getDeviceImei(): Array<String?> {
    val array = arrayOfNulls<String>(2)
    val method = telephonyManager.javaClass.getMethod("getImei", Int::class.java)
    array[0] = method.invoke(telephonyManager, 0) as String
    array[1] = method.invoke(telephonyManager, 1) as String
    return array
}

/**
 * 获取手机Mac地址
 */
fun getMacAddress(): String? {
    Collections.list(NetworkInterface.getNetworkInterfaces()).forEach { networkInterface ->
        if (networkInterface.name.equals("wlan0", true)) {
            val result = StringBuilder()
            networkInterface.hardwareAddress?.forEach { result.append(String.format("%02X:", it)) }
            if (result.isNotEmpty()) result.deleteCharAt(result.length - 1)
            return result.toString()
        }
    }
    return null
}

/**
 * 获取外网的ip地址
 */
fun Context.getIpAddress(): String? {
    Collections.list(NetworkInterface.getNetworkInterfaces()).forEach { networkInterface ->
        if (wifiConnected) {
            if (networkInterface.name.equals("wlan0", true)) {
                val ip = networkInterface.inetAddresses.nextElement().hostAddress
                return ip.substring(0, ip.indexOf("%"))
            }
        } else
            Collections.list(networkInterface.inetAddresses).forEach { inetAddress ->
                if (!inetAddress.isLoopbackAddress && !inetAddress.isLinkLocalAddress) {
                    return inetAddress.hostAddress
                }
            }
    }
    return null
}

/**
 * 获取WIFI下内网的ip地址
 */
fun Context.getWifiAddress(): String? {
    if (wifiConnected) {
        val ip = wifiManager.connectionInfo.ipAddress
        return (ip and 0xFF).toString() + "." + (ip shr 8 and 0xFF) + "." + (ip shr 16 and 0xFF) + "." + (ip shr 24 and 0xFF)
    }
    return null
}

/**
 * 判断是否是魅族Flyme v4及以上系统
 */
fun isFlymeV4OrAbove(): Boolean {
    val displayId = Build.DISPLAY
    if (!TextUtils.isEmpty(displayId) && displayId.contains("Flyme")) {
        val displayIdArray = displayId.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (temp in displayIdArray) {
            if (temp.matches("^[4-9]\\.(\\d+\\.)+\\S*".toRegex())) return true
        }
    }
    return false
}



