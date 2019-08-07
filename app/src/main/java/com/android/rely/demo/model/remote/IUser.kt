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

package com.android.rely.demo.model.remote

import com.android.rely.demo.model.bean.User
import com.dugang.rely.retrofit.Result
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


/**
 * Created by dugang on 2017/10/13.账户相关的接口
 */
interface IUser {
    /**
     * 刷新token
     */

    @FormUrlEncoded
    @POST("api/user/refreshToken")
    fun refreshToken(@Field("refresh_token") refreshToken: String): Call<Result<String>>

    @FormUrlEncoded
    @POST("api/user/login")
    fun login(@Field("userName") userName: String,
              @Field("password") password: String): Observable<Result<User>>

}