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

import android.app.Dialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.rxjava.rxlife.ObservableLife
import com.rxjava.rxlife.RxLife
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * Created by dugang on 2017/7/27.RxJava工具类
 */
data class Result<T>(var code: Int, var msg: String, var data: T)


fun <T> Observable<T>.applySchedulers(): Observable<T> {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.applySchedulers(lifecycleOwner: LifecycleOwner): ObservableLife<T>? {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(RxLife.`as`(lifecycleOwner))
}

fun <T> Observable<T>.applySchedulers(lifecycleOwner: LifecycleOwner, isShowLoading: MutableLiveData<Boolean>, delay: Long = 1
): ObservableLife<T>? {
    return delay(delay, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isShowLoading.postValue(true) }
            .doOnTerminate { isShowLoading.postValue(false) }
            .`as`(RxLife.`as`(lifecycleOwner))
}

fun <T> Observable<T>.applySchedulers(lifecycleOwner: LifecycleOwner, dialog: Dialog?, delay: Long = 1): ObservableLife<T>? {
    return delay(delay, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { disposable ->
                dialog?.setOnCancelListener { disposable.dispose() }
                dialog?.show()
            }
            .doOnTerminate { dialog?.dismiss() }
            .`as`(RxLife.`as`(lifecycleOwner))
}
