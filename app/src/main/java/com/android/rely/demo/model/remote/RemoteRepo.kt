/*
 *      Copyright (c) 2017-2019 dugang
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 */

package com.android.rely.demo.model.remote

import com.dugang.rely.Rely
import com.dugang.rely.common.extension.getRandomStr
import com.android.rely.demo.util.TokenInterceptor
import com.dugang.rely.retrofit.RetrofitBuilder

/**
 * Created by dugang on 2017/10/13.Api
 */
object RemoteRepo {
    private const val release_url = "http://192.168.2.83/DriveCollect/"
    private const val debug_url = "http://192.168.20.215:8000/"

    private val httpBuilder = RetrofitBuilder()
            .baseUrl(if (Rely.isDebug) debug_url else release_url)
            .addHeader("Authentication", getRandomStr())
            .addHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            .addParam("auth", getRandomStr())
//            .addInterceptor(TokenInterceptor())
            .build()


    /**
     * 账户接口
     */
    fun getUser(): IUser = httpBuilder.create(IUser::class.java)
}