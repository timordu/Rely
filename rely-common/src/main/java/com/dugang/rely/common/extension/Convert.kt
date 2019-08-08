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

import android.net.Uri
import android.util.ArrayMap
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.full.memberProperties

fun String.urlEncode(charset: String = "UTF-8"): String = URLEncoder.encode(this, charset)

fun String.urlDecode(charset: String = "UTF-8"): String = URLDecoder.decode(this, charset)

fun Double.format(pattern: String = "#0.00"): Double = format2Str(pattern).toDouble()

fun Double.format2Str(pattern: String = "#0.00"): String = DecimalFormat(pattern).format(this)


//时间格式化成指定格式字符串
fun Long.format2Str(format: String = "yyyy-MM-dd HH:mm:ss"): String = Date(this).format2Str(format)

//日期格式化成指定格式字符串
fun Date.format2Str(format: String = "yyyy-MM-dd HH:mm:ss"): String = SimpleDateFormat(format, Locale.getDefault()).format(this)

//解析字符串成时间毫秒
fun String.parseStr2Mills(format: String = "yyyy-MM-dd HH:mm:ss"): Long = parseStr2Date(format)!!.time

//解析字符串成日期
fun String.parseStr2Date(format: String = "yyyy-MM-dd HH:mm:ss"): Date? = SimpleDateFormat(format, Locale.getDefault()).parse(this)


/**
 * ByteArray转换成16进制字符串
 */
fun ByteArray.toHex(): String {
    val result = StringBuilder()
    forEach {
        result.append(Character.forDigit((it.toInt() shr 4) and 0xF, 16))
        result.append(Character.forDigit(it.toInt() and 0xF, 16))
    }
    return result.toString()
}

/**
 * 16进制字符串转换成ByteArray
 */
fun String.hexToByteArray(): ByteArray {
    val data = ByteArray(length / 2)
    for (i in 0 until length step 2)
        data[i / 2] = ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
    return data
}

/**
 * String转Uri
 */
fun String.toUri(): Uri = Uri.parse(this)

/**
 * 对象转Map
 */
fun Any.toMap(): ArrayMap<String, Any> {
    val arrayMap = ArrayMap<String, Any>()
    javaClass.kotlin.memberProperties.forEach {
        arrayMap[it.name] = it.get(this)
    }
    return arrayMap
}
