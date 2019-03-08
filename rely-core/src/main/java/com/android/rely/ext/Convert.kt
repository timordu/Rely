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

import com.google.gson.Gson
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 将对象转成json
 */
fun Any?.toJson(): String = Gson().toJson(this)

/**
 * 将json转成对象
 */
fun <T> String.jsonToObj(clazz: Class<T>): T = Gson().fromJson(this, clazz)

/**
 * 将json转成Map
 */
fun <T, E> String.jsonToMap(clazz1: Class<T>, clazz2: Class<E>): Map<T, E> = Gson().fromJson(this, type(Map::class.java, clazz1, clazz2))

/**
 * 将json转成List
 */
fun <T> String.jsonToyList(clazz: Class<T>): List<T> = Gson().fromJson(this, type(List::class.java, clazz))


private fun type(raw: Class<*>, vararg args: Type): ParameterizedType {
    return object : ParameterizedType {
        override fun getRawType(): Type = raw

        override fun getOwnerType(): Type? = null

        override fun getActualTypeArguments(): Array<out Type> = args
    }
}
