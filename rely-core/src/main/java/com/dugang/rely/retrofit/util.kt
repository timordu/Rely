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

package com.dugang.rely.retrofit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dugang.rely.Rely
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException


/**
 * Created by dugang on 2017/7/27.RxJava工具类
 */
data class Result<T>(var code: Int, var message: String, var data: T)


fun <T> ViewModel.callApi(
    onStart: (() -> Unit)? = null,
    onRequest: suspend () -> T,
    onSuccess: suspend (T) -> Unit,
    onError: suspend (Int, String) -> Unit,
    onComplete: (() -> Unit)? = null
) {
    viewModelScope.launch {
        withContext(Dispatchers.Main) { onStart?.invoke() }
        try {
            val result = withContext(Dispatchers.IO) { return@withContext onRequest.invoke() }
            withContext(Dispatchers.Main) { onSuccess.invoke(result) }
        } catch (e: Exception) {
            var code = Rely.NET_CODE_ERROR
            val message = when (e) {
                is SocketTimeoutException -> Rely.SOCKET_TIMEOUT_EXCEPTION
                is ConnectException -> Rely.CONNECT_EXCEPTION
                is UnknownHostException -> Rely.UNKNOWN_HOST_EXCEPTION
                is EOFException -> Rely.EMPTY_RESPONSE_EXCEPTION
                is JsonSyntaxException -> Rely.JSON_SYNTAX_EXCEPTION
                is TimeoutException -> Rely.TIMEOUT_EXCEPTION
                is HttpException -> {
                    code = e.code()
                    e.message()
                }
                else -> Rely.UNKNOWN_EXCEPTION
            }
            withContext(Dispatchers.Main) { onError.invoke(code, message) }
        } finally {
            //延时1秒结束,避免loading动画执行不完整
            kotlinx.coroutines.delay(1000)
            withContext(Dispatchers.Main) { onComplete?.invoke() }
        }
    }
}

fun <T> ViewModel.callApi2(
    onStart: (() -> Unit)? = null,
    onRequest: suspend () -> Result<T>,
    onSuccess: suspend (T) -> Unit,
    onError: suspend (Int, String) -> Unit,
    onComplete: (() -> Unit)? = null
) {
    viewModelScope.launch {
        withContext(Dispatchers.Main) { onStart?.invoke() }
        try {
            val result = withContext(Dispatchers.IO) { return@withContext onRequest.invoke() }
            withContext(Dispatchers.Main) {
                if (result.code == Rely.NET_CODE_SUCCESS)
                    onSuccess.invoke(result.data)
                else
                    onError(result.code, result.message)
            }
        } catch (e: Exception) {
            var code = Rely.NET_CODE_ERROR
            val message = when (e) {
                is SocketTimeoutException -> Rely.SOCKET_TIMEOUT_EXCEPTION
                is ConnectException -> Rely.CONNECT_EXCEPTION
                is UnknownHostException -> Rely.UNKNOWN_HOST_EXCEPTION
                is EOFException -> Rely.EMPTY_RESPONSE_EXCEPTION
                is JsonSyntaxException -> Rely.JSON_SYNTAX_EXCEPTION
                is TimeoutException -> Rely.TIMEOUT_EXCEPTION
                is HttpException -> {
                    code = e.code()
                    e.message()
                }
                else -> Rely.UNKNOWN_EXCEPTION
            }
            withContext(Dispatchers.Main) { onError.invoke(code, message) }
        } finally {
            //延时1秒结束,避免loading动画执行不完整
            kotlinx.coroutines.delay(1000)
            withContext(Dispatchers.Main) { onComplete?.invoke() }
        }

    }
}