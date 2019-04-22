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

import android.app.Dialog
import com.android.rely.demo.model.bean.User
import com.android.rely.demo.model.remote.RemoteRepo
import com.android.rely.mvvm.base.BaseViewModel
import com.android.rely.mvvm.widget.LoadingDialog
import com.android.rely.retrofit.applySchedulers
import com.android.rely.retrofit.result.ResultObserver

/**
 * Created by dugang on 2019-04-21.
 */
class TestViewModel : BaseViewModel() {

    fun login(userName: String, password: String) {
//        RemoteRepo.getUser()
//                .login(userName, password)
//                .applySchedulers(mLifecycleOwner, isShowLoading)
//                ?.subscribe(object : ResultObserver<User>() {
//                    override fun handlerSuccess(data: User) {
//
//                    }
//                })
    }

    fun login(userName: String, password: String, loadingDialog: Dialog) {
        RemoteRepo.getUser()
                .login(userName, password)
                .applySchedulers()
                .subscribe(object : ResultObserver<User>() {
                    override fun handlerSuccess(data: User) {

                    }
                })

    }
}