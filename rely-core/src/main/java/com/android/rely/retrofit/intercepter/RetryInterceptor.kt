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

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by dugang on 2017/8/3.请求失败重试拦截器
 */
class RetryInterceptor(private var retryCount: Int, private var retryDelay: Int) : Interceptor {
    private var currentRetry = 0

    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.proceed(chain.request())
        while (!response.isSuccessful && currentRetry < retryCount) {
            currentRetry++
            Thread.sleep(retryDelay * 1000L)
            response = chain.proceed(chain.request())
        }
        return response
    }
}