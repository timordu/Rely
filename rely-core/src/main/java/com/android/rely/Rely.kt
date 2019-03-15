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

package com.android.rely

import android.annotation.SuppressLint
import android.content.Context
import com.blankj.ALog

@SuppressLint("StaticFieldLeak")
object Rely {
    const val CONNECT_EXCEPTION = "网络连接异常，请检查您的网络状态"
    const val SOCKET_TIMEOUT_EXCEPTION = "网络连接超时，请检查您的网络状态，稍后重试"
    const val UNKNOWN_HOST_EXCEPTION = "与服务器连接失败"
    const val JSON_SYNTAX_EXCEPTION = "对象类型错误,无法解析"
    const val EMPTY_RESPONSE_EXCEPTION = "无效返回"
    const val UNKNOWN_EXCEPTION = "未知异常"

    var NET_CODE_SUCCESS: Int = 0
    var NET_CODE_ERROR = -1

    lateinit var appContext: Context
    var isDebug: Boolean = true

    /**
     * 初始化工具类
     */
    fun init(context: Context, debug: Boolean) {
        appContext = context.applicationContext
        isDebug = debug
        //初始化日志工具
        ALog.init(context).setLogSwitch(debug)
    }

    fun initNetCode(success: Int = 0, error: Int = -1) {
        NET_CODE_SUCCESS = success
        NET_CODE_ERROR = error
    }
}