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

package com.dugang.rely.retrofit.result

import com.dugang.rely.Rely
import com.dugang.rely.eventbus.MsgEvent
import com.dugang.rely.eventbus.post
import com.dugang.rely.retrofit.Result
import com.google.gson.JsonSyntaxException
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by dugang on 2017/8/4.ResultObserver
 */
abstract class ResultObserver<T> : Observer<Result<T>> {

    override fun onSubscribe(disposable: Disposable) {}

    override fun onComplete() {}

    override fun onNext(t: Result<T>) {
        if (t.code == Rely.NET_CODE_SUCCESS) {
            if (t.data == null)
                handlerError(Rely.NET_CODE_ERROR, Rely.EMPTY_RESPONSE_EXCEPTION)
            else
                handlerSuccess(t.data)
        } else
            handlerError(t.code, t.msg)
    }

    abstract fun handlerSuccess(data: T)

    override fun onError(throwable: Throwable) {
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