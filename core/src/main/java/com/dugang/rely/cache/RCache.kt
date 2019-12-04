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

package com.dugang.rely.cache

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.dugang.rely.Rely
import com.dugang.rely.common.extension.*
import com.dugang.rely.extension.jsonToMap
import com.dugang.rely.extension.jsonToObj
import com.dugang.rely.extension.jsonToyList
import com.dugang.rely.extension.toJson
import com.jakewharton.disklrucache.DiskLruCache
import java.io.ByteArrayOutputStream
import java.io.File


/**
 * Created by dugang on 2017/9/25.RCache
 */
@Suppress("unused")
class RCache(cacheDir: File, max_size: Long, max_count: Int, appVersion: Int) {
    companion object {
        const val TIME_MINUTE = 60
        const val TIME_HOUR = 60 * 60
        const val TIME_DAY = 60 * 60 * 24

        private const val MAX_SIZE = 1024 * 1024 * 50L // 50 mb
        private const val MAX_COUNT = 1 // 单个节点所对应的数据的个数
        private val mInstanceMap = HashMap<String, RCache>()

        /**
         * 获取RCache实例
         *
         * @param cacheDir 缓存目录,默认为"RCache"
         * @param max_size 最大缓存空间
         * @param max_count 最大缓存数量
         */
        fun get(cacheDir: String = "RCache", max_size: Long = MAX_SIZE, max_count: Int = MAX_COUNT): RCache =
            get(
                File(Rely.appContext.DATA_DIR_PATH, cacheDir),
                max_size,
                max_count
            )

        /**
         * 获取RCache实例
         *
         * @param cacheDir 缓存目录
         * @param max_size 最大缓存空间
         * @param max_count 最大缓存数量
         */
        fun get(cacheDir: File, max_size: Long = MAX_SIZE, max_count: Int = MAX_COUNT): RCache {
            val key = cacheDir.absolutePath.md5()
            var rCache = mInstanceMap[key]
            if (rCache == null) {
                rCache = RCache(cacheDir, max_size, max_count, 1)
                mInstanceMap[key] = rCache
            }
            return rCache
        }
    }

    private var diskLruCache: DiskLruCache

    init {
        if (!cacheDir.exists()) cacheDir.mkdirs()
        diskLruCache = DiskLruCache.open(cacheDir, appVersion, max_count, max_size)
    }


    /*
        ---------- ByteArray数据读写 ----------
    */

    /**
     * 保存ByteArray类型数据,如果传入null,则移除该key的缓存
     */
    @JvmOverloads
    fun put(key: String, value: ByteArray?, saveTime: Int = Int.MAX_VALUE) {
        if (value.isNotNull()) {
            val sb = StringBuilder().apply {
                append(currentTime + saveTime * 1000L)
                append(" ")
                append(value!!.encryptByAES(MY_UUID).base64Encode2Str())
            }

            val editor = diskLruCache.edit(key.md5()).apply {
                set(0, sb.toString())
            }
            editor.commit()
        } else
            remove(key)
    }

    /**
     * 保存String类型数据,如果传入null,则移除该key的缓存
     */
    @JvmOverloads
    fun put(key: String, value: String?, saveTime: Int = Int.MAX_VALUE) {
        if (value.isNotNull())
            put(key, value!!.toByteArray(), saveTime)
        else
            remove(key)
    }

    /**
     * 保存Boolean类型数据,如果传入null,则移除该key的缓存
     */
    fun put(key: String, value: Boolean?, saveTime: Int = Int.MAX_VALUE) {
        if (value.isNotNull())
            put(key, value.toString(), saveTime)
        else
            remove(key)
    }

    /**
     * 保存Int类型数据,如果传入null,则移除该key的缓存
     */
    fun put(key: String, value: Int?, saveTime: Int = Int.MAX_VALUE) {
        if (value.isNotNull())
            put(key, value.toString(), saveTime)
        else
            remove(key)
    }

    /**
     * 保存Long类型数据,如果传入null,则移除该key的缓存
     */
    fun put(key: String, value: Long?, saveTime: Int = Int.MAX_VALUE) {
        if (value.isNotNull())
            put(key, value.toString(), saveTime)
        else
            remove(key)
    }

    /**
     * 保存Float类型数据,如果传入null,则移除该key的缓存
     */
    fun put(key: String, value: Float?, saveTime: Int = Int.MAX_VALUE) {
        if (value.isNotNull())
            put(key, value.toString(), saveTime)
        else
            remove(key)
    }

    /**
     * 保存Double类型数据,如果传入null,则移除该key的缓存
     */
    fun put(key: String, value: Double?, saveTime: Int = Int.MAX_VALUE) {
        if (value.isNotNull())
            put(key, value.toString(), saveTime)
        else
            remove(key)
    }

    /**
     * 保存Bitmap类型数据,如果传入null,则移除该key的缓存
     */
    @JvmOverloads
    fun put(key: String, value: Bitmap?, saveTime: Int = Int.MAX_VALUE) {
        if (value.isNotNull()) {
            val baos = ByteArrayOutputStream()
            value!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
            put(key, baos.toByteArray(), saveTime)
        } else
            remove(key)
    }

    /**
     * 保存Drawable类型数据,如果传入null,则移除该key的缓存
     */
    fun put(key: String, value: Drawable?, saveTime: Int = Int.MAX_VALUE) {
        if (value.isNotNull())
            put(key, value!!.toBitmap(), saveTime)
        else
            remove(key)
    }

    /**
     * 保存Object数据,如果传入null,则移除该key的缓存
     */
    fun <T : Parcelable> put(key: String, value: T?, saveTime: Int = Int.MAX_VALUE) {
        if (value.isNotNull())
            put(key, value.toJson(), saveTime)
        else
            remove(key)
    }

    /**
     * 保存List数据,如果传入null,则移除该key的缓存
     */
    fun <T> put(key: String, value: List<T>?, saveTime: Int = Int.MAX_VALUE) {
        if (value.isNotNull())
            put(key, value.toJson(), saveTime)
        else
            remove(key)
    }

    /**
     * 保存Map类型数据,如果传入null,则移除该key的缓存
     */
    fun <T, E> put(key: String, value: Map<T, E>?, saveTime: Int = Int.MAX_VALUE) {
        if (value.isNotNull())
            put(key, value.toJson(), saveTime)
        else
            remove(key)
    }

    /**
     * 获取ByteArray类型数据
     */
    fun getAsByteArray(key: String): ByteArray? {
        val data = diskLruCache.get(key.md5())?.getString(0)?.split(" ")
        data?.let {
            if (currentTime <= it[0].toLong()) {
                return it[1].base64Decode().decryptByAES(MY_UUID)
            }
        }
        return null
    }

    /**
     * 获取String类型的数据
     */
    fun getAsString(key: String): String? = getAsByteArray(key)?.let { String(it) }

    /**
     * 获取Boolean类型的数据
     */
    fun getAsBoolean(key: String): Boolean? = getAsString(key)?.toBoolean()

    /**
     * 获取Int类型的数据
     */
    fun getAsInt(key: String): Int? = getAsString(key)?.toInt()

    /**
     * 获取Long类型的数据
     */
    fun getAsLong(key: String): Long? = getAsString(key)?.toLong()

    /**
     * 获取Float类型的数据
     */
    fun getAsFloat(key: String): Float? = getAsString(key)?.toFloat()

    /**
     * 获取Double类型的数据
     */
    fun getAsDouble(key: String): Double? = getAsString(key)?.toDouble()

    /**
     * 读取Object数据
     */
    fun <T> getAsObject(key: String, clazz: Class<T>): T? = getAsString(key)?.jsonToObj(clazz)

    /**
     * 读取List数据
     */
    fun <T> getAsList(key: String, clazz: Class<T>): List<T>? = getAsString(key)?.jsonToyList(clazz)

    /**
     * 读取Map数据,value暂不支持Any类型的值
     */
    fun <T, E> getAsMap(key: String, clazz1: Class<T>, clazz2: Class<E>): Map<T, E>? =
        getAsString(key)?.jsonToMap(clazz1, clazz2)

    /**
     * 获取Bitmap类型的数据
     */
    fun getAsBitmap(key: String): Bitmap? = getAsByteArray(key)?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }

    /**
     * 移除key
     */
    fun remove(vararg keys: String) {
        keys.forEach { diskLruCache.remove(it.md5()) }
    }

    /**
     * 缓存大小
     */
    fun size(): String = diskLruCache.size().formatFileLength()

    /**
     * 清空缓存
     */
    fun clear() {
        diskLruCache.delete()
    }
}