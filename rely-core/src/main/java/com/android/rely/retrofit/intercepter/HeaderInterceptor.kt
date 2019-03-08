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

package com.android.rely.retrofit.intercepter

import android.util.ArrayMap
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by dugang on 2017/3/15.请求头拦截器
 */

class HeaderInterceptor(private val headers: ArrayMap<String, String>) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = chain.request().newBuilder()
        headers.keys.forEach { key -> headers[key]?.let { requestBuilder.header(key, it) } }
        val responseBuilder = chain.proceed(requestBuilder.build()).newBuilder()
        //客户端自主添加缓存控制
        if (!request.cacheControl().toString().isEmpty()) {
            responseBuilder
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + request.cacheControl().maxAgeSeconds())
        }
        return responseBuilder.build()
    }
}
