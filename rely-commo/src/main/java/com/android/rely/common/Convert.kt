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

import android.net.Uri
import android.util.ArrayMap

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

fun String.toUri(): Uri = Uri.parse(this)

fun Any.toMap(): ArrayMap<String, Any> {
    val arrayMap = ArrayMap<String, Any>()
    val declaredFields = javaClass.declaredFields
    for (field in declaredFields) {
        val isAccessible = field.isAccessible
        field.isAccessible = true
        arrayMap[field.name] = field.get(this)
        field.isAccessible = isAccessible
    }
    arrayMap.remove("serialVersionUID")
    arrayMap.remove("Companion")
    arrayMap.remove("\$change")
    arrayMap.remove("CREATOR")
    return arrayMap
}
