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

import java.net.URLDecoder
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by dugang on 2017/12/11. 格式化扩展函数
 */

fun String.urlEncode(charset: String = "UTF-8"): String = URLEncoder.encode(this, charset)

fun String.urlDecode(charset: String = "UTF-8"): String = URLDecoder.decode(this, charset)

fun Double.format(pattern: String = "#0.00"): Double = format2Str(pattern).toDouble()

fun Double.format2Str(pattern: String = "#0.00"): String = DecimalFormat(pattern).format(this)


//时间格式化成指定格式字符串
fun Long.format2Str(format: String = "yyyy-MM-dd HH:mm:ss"): String =
        Date(this).format2Str(format)

//日期格式化成指定格式字符串
fun Date.format2Str(format: String = "yyyy-MM-dd HH:mm:ss"): String =
        SimpleDateFormat(format, Locale.getDefault()).format(this)

//解析字符串成时间毫秒
fun String.parseStr2Mills(format: String = "yyyy-MM-dd HH:mm:ss"): Long =
        parseStr2Date(format).time

//解析字符串成日期
fun String.parseStr2Date(format: String = "yyyy-MM-dd HH:mm:ss"): Date =
        SimpleDateFormat(format, Locale.getDefault()).parse(this)

// 文件大小格式化
fun Long.formatFileLength(): String =
        when {
            this < 1024 -> String.format("%d B", this)
            this < 1024 * 1024 -> String.format("%d KB", this / 1024)
            this < 1024 * 1024 * 1024 -> String.format("%.1fMB", this.toDouble() / 1024 / 1024)
            else -> String.format("%.1fGB", this.toDouble() / 1024 / 1024 / 1024)
        }


