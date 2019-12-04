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
import android.net.NetworkCapabilities
import android.provider.Settings

/*
  ---------- 网络相关扩展函数 ----------
 */

/**
 * 判断当前是否已有效连接
 */
val Context.netWorkConnected: Boolean
    @TargetApi(28)
    get() {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

/**
 * 判断当前有效连接是蜂窝数据
 */
val Context.cellularConnected: Boolean
    @TargetApi(28)
    get() {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

/**
 * 判断当前有效连接是WIFI
 */
val Context.wifiConnected: Boolean
    @TargetApi(28)
    get() {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }


/**
 * 判断/设置WIFI状态, get()-获取WIFI状态,set()-设置wifi状态
 */
var Context.isWifiEnable: Boolean
    get() = wifiManager.isWifiEnabled
    set(value) {
        wifiManager.isWifiEnabled = value
    }

/**
 * 打开无线设置界面
 */
fun Context.openWirelessSettings() {
    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}

