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

import java.io.Closeable
import java.util.*

/**
 * Created by dugang on 2017/10/7.其它扩展函数
 */


/**
 * 获取随机字符串,默认32位长度
 */
fun getRandomStr(length: Int = 32): String {
    val str = charArrayOf(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    val random = Random()
    val sb = StringBuilder()
    while (sb.length <= length) {
        sb.append(str[random.nextInt(str.size)])
    }
    return sb.toString()
}

/**
 * 关闭IO流
 */
fun <T : Closeable> closeIO(vararg ios: T?) {
    ios.forEach { it?.close() }
}
