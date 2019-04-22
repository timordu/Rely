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

package com.android.rely.retrofit.result

import com.android.rely.Rely
import com.android.rely.eventbus.MsgEvent
import com.android.rely.eventbus.post
import com.android.rely.retrofit.Result
import com.google.gson.JsonSyntaxException
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by dugang on 2017/4/18.ResultCallBack
 */
abstract class ResultCallBack<T> : Callback<Result<T>> {
    override fun onResponse(call: Call<Result<T>>, response: Response<Result<T>>?) {
        response?.let {
            it.body()?.let { body ->
                if (body.code == Rely.NET_CODE_SUCCESS) {
                    if (body.data == null)
                        handlerError(Rely.NET_CODE_ERROR, Rely.EMPTY_RESPONSE_EXCEPTION)
                    else
                        handlerSuccess(body.data)
                } else {
                    handlerError(body.code, body.msg)
                }
            }
        }
    }

    abstract fun handlerSuccess(data: T)

    override fun onFailure(call: Call<Result<T>>, throwable: Throwable) {
        var code = Rely.NET_CODE_ERROR
        val msg = when (throwable) {
            is SocketTimeoutException -> Rely.SOCKET_TIMEOUT_EXCEPTION
            is ConnectException -> Rely.CONNECT_EXCEPTION
            is UnknownHostException -> Rely.UNKNOWN_HOST_EXCEPTION
            is EOFException -> Rely.EMPTY_RESPONSE_EXCEPTION
            is JsonSyntaxException -> Rely.JSON_SYNTAX_EXCEPTION
            is HttpException -> {
                code = throwable.code()
                throwable.message()
            }
            else -> throwable.message ?: Rely.UNKNOWN_EXCEPTION
        }
        handlerError(code, msg)
    }

    open fun handlerError(code: Int, msg: String) {
        MsgEvent(code, msg).post()
    }
}
