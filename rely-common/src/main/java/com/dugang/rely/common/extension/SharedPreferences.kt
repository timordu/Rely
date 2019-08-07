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

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by dugang on 2018/5/14.
 */

/**
 * 获取SharedPreferences,null返回默认的SharedPreferences
 */
fun Context.getSP(spName: String? = null): SharedPreferences {
    return if (spName == null) {
        PreferenceManager.getDefaultSharedPreferences(this)
    } else {
        getSharedPreferences(spName, 0)
    }
}

/**
 * 保存SharedPreferences，根据传入类型自动转换
 */
fun <T : Comparable<T>> Context.setSP(keyName: String, value: T, spName: String? = null) {
    getSP(spName).edit().apply {
        when (value) {
            is Boolean -> putBoolean(keyName, value as Boolean)
            is Int -> putInt(keyName, value as Int)
            is String -> putString(keyName, value as String)
            is Float -> putFloat(keyName, value as Float)
            is Long -> putLong(keyName, value as Long)
        }
    }.apply()
}

/**
 * 移除SharedPreferences
 */
fun Context.removeSP(key: String, spName: String? = null) {
    getSP(spName).edit().remove(key).apply()
}

/**
 * 清空SharedPreferences
 */
fun Context.clearSP(spName: String? = null) {
    getSP(spName).edit().clear().apply()
}
