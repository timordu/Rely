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

import java.util.*
import kotlin.math.abs


//当前日期
val currentDate: Date get() = Date()

//当前时间
val currentTime: Long get() = System.currentTimeMillis()


//计算两个日期间隔天数( < 0 表示日期1在日期2之前, = 0 表示是同一天, > 0 表示日期1在日期2之后)
fun dateInterval(date1: Long, date2: Long): Long = (date1 - date2) / (1000 * 3600 * 24)

fun dateInterval(date1: Date, date2: Date): Long = dateInterval(date1.time, date2.time)

fun dateInterval(date1: String, date2: String, format: String = "yyyy-MM-dd"): Long =
        dateInterval(date1.parseStr2Mills(format), date2.parseStr2Mills(format))

//计算日期和当前日期比较结果
fun Long.dateIntervalByNow(): String {
    val interval = dateInterval(format2Str("yyyy-MM-dd"), currentTime.format2Str("yyyy-MM-dd"))
    return when {
        interval < 0 -> "${abs(interval)}天前"
        interval > 0 -> "${abs(interval)}天后"
        else -> "今天"
    }
}

fun Date.dateIntervalByNow(): String = time.dateIntervalByNow()

fun String.dateIntervalByNow(format: String = "yyyy-MM-dd"): String = parseStr2Mills(format).dateIntervalByNow()

//计算两个时间间隔秒数,要求都为毫秒级( < 0 表示时间1在时间2之前, = 0 表示是同一秒, > 0 表示时间1在时间2之后)
fun timeInterval(time1: Long, time2: Long): Long = (time1 - time2) / 1000

fun timeInterval(time1: String, time2: String, format: String = "yyyy-MM-dd HH:mm:ss"): Long =
        timeInterval(time1.parseStr2Mills(format), time2.parseStr2Mills(format))

//计算时间和当前时间比较结果,要求毫秒级
fun Long.timeIntervalByNow(): String {
    return when (val interval = timeInterval(currentTime, this)) {
        in 0..59 -> "刚刚"
        in 60..3599 -> "${interval / 60}分钟前"
        in 3600..3600 * 24 -> "${interval / 3600}小时前"
        else -> format2Str("MM-dd HH:mm")
    }
}

fun Date.timeIntervalByNow(): String = time.timeIntervalByNow()

fun String.timeIntervalByNow(format: String = "yyyy-MM-dd HH:mm:ss"): String = parseStr2Mills(format).timeIntervalByNow()