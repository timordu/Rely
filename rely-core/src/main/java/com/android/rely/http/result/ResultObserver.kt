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

package com.android.rely.library.core.http.result

import com.android.rely.library.RelyConfig
import com.android.rely.eventbus.MsgEvent
import com.android.rely.http.Result
import com.google.gson.JsonSyntaxException
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
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
        if (t.code == RelyConfig.NET_CODE_SUCCESS) {
            if (t.data == null)
                handlerError(RelyConfig.NET_CODE_ERROR, RelyConfig.EMPTY_RESPONSE_EXCEPTION)
            else
                handlerSuccess(t.data)
        } else
            handlerError(t.code, t.msg)
    }

    abstract fun handlerSuccess(data: T)

    override fun onError(throwable: Throwable) {
        handlerError(RelyConfig.NET_CODE_ERROR, when (throwable) {
            is SocketTimeoutException -> RelyConfig.SOCKET_TIMEOUT_EXCEPTION
            is ConnectException -> RelyConfig.CONNECT_EXCEPTION
            is UnknownHostException -> RelyConfig.UNKNOWN_HOST_EXCEPTION
            is EOFException -> RelyConfig.EMPTY_RESPONSE_EXCEPTION
            is JsonSyntaxException -> RelyConfig.JSON_SYNTAX_EXCEPTION
            else -> throwable.message ?: RelyConfig.UNKNOWN_EXCEPTION
        })
    }

    open fun handlerError(code: Int, msg: String) {
        EventBus.getDefault().post(MsgEvent(code, msg))
    }

}