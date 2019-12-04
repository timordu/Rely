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

package com.dugang.rely.retrofit.intercepter

import android.util.ArrayMap
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by dugang on 2017/3/15. 公共请求参数拦截器
 */
class ParamInterceptor(private val params: ArrayMap<String, String>) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()
        val newRequestBuilder = oldRequest.newBuilder()
        when (oldRequest.method) {
            "GET" -> {//GET请求则使用HttpUrl.Builder来构建
                val httpUrlBuilder = oldRequest.url.newBuilder()
                params.keys.forEach { httpUrlBuilder.addQueryParameter(it, params[it]) }
                newRequestBuilder.url(httpUrlBuilder.build())
            }
            "POST" -> {
                //如果原请求是表单请求
                if (oldRequest.body is FormBody) {
                    val formBodyBuilder = FormBody.Builder()
                    //公共请求参数
                    params.keys.forEach { key ->
                        params[key]?.let { formBodyBuilder.add(key, it) }
                    }

                    //原请求参数
                    val oldFormBody = oldRequest.body as FormBody
                    for (i in 0 until oldFormBody.size) {
                        formBodyBuilder.add(oldFormBody.name(i), oldFormBody.value(i))
                    }
                    newRequestBuilder.post(formBodyBuilder.build())
                }
            }
        }
        return chain.proceed(newRequestBuilder.build())
    }
}
