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

package com.dugang.rely.extension

import android.content.SharedPreferences
import com.dugang.rely.Rely
import com.dugang.rely.common.extension.clearSP
import com.dugang.rely.common.extension.getSP
import com.dugang.rely.common.extension.removeSP
import com.dugang.rely.common.extension.setSP

/**
 * Created by dugang on 2018/5/14.
 */

/**
 * 获取SharedPreferences,null返回默认的SharedPreferences
 */
fun getSP(spName: String? = null): SharedPreferences = Rely.appContext.getSP(spName)

/**
 * 保存SharedPreferences，根据传入类型自动转换
 */
fun <T : Comparable<T>> setSP(keyName: String, value: T, spName: String? = null) {
    Rely.appContext.setSP(keyName, value, spName)
}

/**
 * 移除SharedPreferences
 */
fun removeSP(key: String, spName: String? = null) {
    Rely.appContext.removeSP(key, spName)
}

/**
 * 清空SharedPreferences
 */
fun clearSP(spName: String? = null) {
    Rely.appContext.clearSP(spName)
}
