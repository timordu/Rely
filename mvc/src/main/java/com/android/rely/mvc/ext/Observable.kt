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

package com.android.rely.mvc.ext

import android.app.Dialog
import com.trello.rxlifecycle3.LifecycleProvider
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.applySchedulers(provider: LifecycleProvider<*>, dialog: Dialog?, delay: Long = 1): Observable<T> {
    return delay(delay, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { disposable ->
            dialog?.setOnCancelListener { disposable.dispose() }
            dialog?.show()
        }
        .observeOn(AndroidSchedulers.mainThread())
        .doOnTerminate { dialog?.dismiss() }
        .bindToLifecycle(provider)
}

fun <T> Observable<T>.applySchedulers(provider: LifecycleProvider<*>): Observable<T> {
    return subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .bindToLifecycle(provider)
}