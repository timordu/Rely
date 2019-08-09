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

package com.android.rely.demo.ui.viewmodel

import com.android.rely.demo.model.remote.RemoteRepo
import com.blankj.ALog
import com.dugang.rely.base.BaseViewModel
import com.dugang.rely.extension.toJson
import com.dugang.rely.retrofit.callApi

/**
 * Created by dugang on 2019-04-21.
 */
class MainViewModel : BaseViewModel() {

    fun testNetwork(username: String, password: String) {
        callApi(
                onStart = {
                    ALog.d("call Api----->start")
                },
                onRequest = {
                    ALog.d("call Api----->onRequest")
                    RemoteRepo.getUser().login(username, password)
                },
                onSuccess = {
                    ALog.d(it.toJson())
                    ALog.d("call Api----->onSuccess")
                },
                onError = { code, message ->
                    ALog.d("call Api----->onError $code,$message")
                },
                onComplete = {
                    ALog.d("call Api----->onComplete")
                })

        callApi(
                onStart = {
                    ALog.d("call Api----->start")
                },
                onRequest = {
                    ALog.d("call Api----->onRequest")
                    RemoteRepo.getUser().login2(username, password)
                },
                onSuccess = {
                    ALog.d(it.toJson())
                    ALog.d("call Api----->onSuccess")
                },
                onError = { code, message ->
                    ALog.d("call Api----->onError $code,$message")
                },
                onComplete = {
                    ALog.d("call Api----->onComplete")
                })

    }

}