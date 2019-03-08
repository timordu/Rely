///*
// *      Copyright (c) 2017-2019 dugang
// *
// *      Licensed under the Apache License, Version 2.0 (the "License");
// *      you may not use this file except in compliance with the License.
// *      You may obtain a copy of the License at
// *
// *          http://www.apache.org/licenses/LICENSE-2.0
// *
// *      Unless required by applicable law or agreed to in writing, software
// *      distributed under the License is distributed on an "AS IS" BASIS,
// *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *      See the License for the specific language governing permissions and
// *      limitations under the License.
// *
// */
//
//package com.android.demo.util
//
//import android.text.TextUtils
//import com.android.rely.demo.Contains
//import com.android.demo.model.remote.RemoteRepo
//import com.android.rely.ext.getSP
//import com.android.rely.ext.setSP
//import com.blankj.ALog
//import okhttp3.Interceptor
//import okhttp3.Response
//import okhttp3.ResponseBody
//import org.json.JSONObject
//
///**
// * Created by dugang on 2017/8/30. 对于状态码返回200 业务码返回token过期的刷新token拦截器
// */
//@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
//class TokenInterceptor : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val originalRequest = chain.request()
//        val originalResponse = chain.proceed(originalRequest)
//
//        val contentType = originalResponse.body()?.contentType()
//        val result = originalResponse.body()?.string()
//        val response = originalResponse.newBuilder().body(ResponseBody.create(contentType, result)).build()
//
//        if (!TextUtils.isEmpty(result)) {
//            try {
//                val obj = JSONObject(result)
//                if (obj.has("code") && obj.getInt("code") == Contains.NET_CODE_TOKEN_EXPIRE) {
//                    val refreshToken = getSP().getString(Contains.KEY_REFRESH_TOKEN,null)
//                    var newToken: String? = null
//
//                    refreshToken?.let {
//                        newToken = RemoteRepo.getUser().refreshToken(it).execute().body()!!.data
//                    }
//
//                    newToken?.let {
//                        setSP(Contains.KEY_ACCESS_TOKEN,it)
//                        val requestBuilder = originalRequest.newBuilder().header("Authentication", it).build()
//                        return chain.proceed(requestBuilder)
//                    }
//                }
//            } catch (e: Exception) {
//                ALog.e(e.message!!)
//            }
//        }
//        return response
//    }
//}