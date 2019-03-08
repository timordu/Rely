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

package com.android.rely.ext

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.android.rely.Rely

/**
 * Created by dugang on 2018/5/14.
 */

/**
 * 获取SharedPreferences,null返回默认的SharedPreferences
 */
fun getSP(spName: String? = null): SharedPreferences {
    return if (spName == null) {
        PreferenceManager.getDefaultSharedPreferences(Rely.appContext)
    } else {
        Rely.appContext.getSharedPreferences(spName, 0)
    }
}

/**
 * 保存SharedPreferences，根据传入类型自动转换
 */
fun <T : Comparable<T>> setSP(keyName: String, value: T, spName: String? = null) {
    val edit = getSP(spName).edit()
    when (value) {
        is Boolean -> edit.putBoolean(keyName, value as Boolean)
        is Int -> edit.putInt(keyName, value as Int)
        is String -> edit.putString(keyName, value as String)
        is Float -> edit.putFloat(keyName, value as Float)
        is Long -> edit.putLong(keyName, value as Long)
    }
    edit.apply()
}

/**
 * 移除SharedPreferences
 */
fun removeSP(key: String, spName: String? = null) {
    getSP(spName).edit().remove(key).apply()
}

/**
 * 清空SharedPreferences
 */
fun clearSP(spName: String? = null) {
    getSP(spName).edit().clear().apply()
}
