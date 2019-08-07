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

package com.dugang.rely.retrofit.cookie

import android.text.TextUtils
import com.dugang.rely.common.extension.currentTime
import com.dugang.rely.extension.getSP
import com.dugang.rely.extension.removeSP
import com.dugang.rely.extension.setSP
import com.dugang.rely.extension.toJson
import com.blankj.ALog
import com.google.gson.Gson
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by dugang on 2018/7/5.
 */
class CookieStoreImpl : CookieStore {
    companion object {
        private const val TAG = "CookieStoreImpl"
        private const val COOKIE_PREFS = "http_cookies"
        private const val COOKIE_NAME_PREFIX = "cookie_"
    }

    private var cookiesMap: MutableMap<String, ConcurrentHashMap<String, Cookie>> = mutableMapOf()

    init {
        val all = getSP(COOKIE_PREFS)
            .all
        all.keys.forEach { key ->
            if (!key.startsWith(COOKIE_NAME_PREFIX)) {
                cookiesMap[key] = ConcurrentHashMap()
                all[key].toString().split(",").forEach { cookieToken ->
                    try {
                        val json = all[COOKIE_NAME_PREFIX + cookieToken].toString()
                        val cookie = Gson().fromJson(json, Cookie::class.java)
                        if (cookie.expiresAt < currentTime) {
                            removeCookie(key, cookie)
                        } else {
                            cookiesMap[key]?.put(cookieToken, cookie)
                        }
                    } catch (e: Exception) {
                        ALog.eTag(TAG, e.message)
                    }
                }
            }
        }
    }

    override fun saveCookie(url: HttpUrl, mutableList: MutableList<Cookie>) {
        if (!cookiesMap.containsKey(url.host)) cookiesMap[url.host] = ConcurrentHashMap()
        mutableList.forEach { cookie ->
            if (cookie.expiresAt < System.currentTimeMillis()) {
                removeCookie(url.host, cookie)
            } else {
                val cookieToken = getCookieToken(cookie)
                cookiesMap[url.host]?.let {
                    it[cookieToken] = cookie
                    setSP(
                        url.host,
                        TextUtils.join(",", it.keys),
                        COOKIE_PREFS
                    )
                    setSP(
                        COOKIE_NAME_PREFIX + cookieToken,
                        cookie.toJson(),
                        COOKIE_PREFS
                    )
                }
            }
        }
    }

    override fun loadCookie(url: HttpUrl): MutableList<Cookie> {
        val mutableList = mutableListOf<Cookie>()
        cookiesMap[url.host]?.values?.forEach {
            if (it.expiresAt < currentTime) {
                removeCookie(url.host, it)
            } else {
                mutableList.add(it)
            }
        }
        return mutableList
    }

    override fun removeCookie(host: String, cookie: Cookie?) {
        if (cookiesMap.containsKey(host)) {
            if (cookie != null) {
                val cookieName = getCookieToken(cookie)
                cookiesMap[host]?.let {
                    if (it.containsKey(cookieName)) {
                        removeSP(
                            COOKIE_NAME_PREFIX + cookieName,
                            COOKIE_PREFS
                        )
                        it.remove(cookieName)
                        setSP(
                            host,
                            TextUtils.join(",", it.keys),
                            COOKIE_PREFS
                        )
                    }
                }
            } else {
                //cookie为空,清除url对应的所有cookie缓存
                //文件中的
                cookiesMap[host]?.let { cookieMap ->
                    cookieMap.keys().iterator()?.forEach { key ->
                        cookieMap[key]?.let {
                            removeSP(
                                COOKIE_NAME_PREFIX + getCookieToken(
                                    it
                                ), COOKIE_PREFS
                            )
                        }
                    }
                    removeSP(
                        host,
                        COOKIE_PREFS
                    )
                    //内存中的
                    cookiesMap.remove(host)
                }
            }
        }
    }

    private fun getCookieToken(cookie: Cookie): String {
        return cookie.name + "@" + cookie.domain
    }
}