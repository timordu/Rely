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

package com.android.rely.mvvm.base

import android.content.Intent
import androidx.lifecycle.*
import com.android.rely.eventbus.MsgEvent
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Suppress("unused")
abstract class BaseViewModel : ViewModel(), LifecycleObserver, LifecycleScopeProvider<Lifecycle.Event> {
    private val lifecycleEvents = BehaviorSubject.createDefault(Lifecycle.Event.ON_CREATE)

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        EventBus.getDefault().register(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        EventBus.getDefault().unregister(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    open fun onLifecycleChanged(owner: LifecycleOwner, event: Lifecycle.Event) {
    }


    override fun lifecycle(): Observable<Lifecycle.Event> = lifecycleEvents.hide()

    override fun correspondingEvents(): CorrespondingEventsFunction<Lifecycle.Event> {
        return CorrespondingEventsFunction { lastEvent ->
            return@CorrespondingEventsFunction when (lastEvent) {
                Lifecycle.Event.ON_CREATE -> Lifecycle.Event.ON_DESTROY
                Lifecycle.Event.ON_START -> Lifecycle.Event.ON_STOP
                Lifecycle.Event.ON_RESUME -> Lifecycle.Event.ON_PAUSE
                Lifecycle.Event.ON_PAUSE -> Lifecycle.Event.ON_STOP
                else ->
                    throw LifecycleEndedException("Lifecycle has ended! Last event was $lastEvent")
            }
        }
    }


    override fun peekLifecycle(): Lifecycle.Event? = lifecycleEvents.value


    override fun onCleared() {
        lifecycleEvents.onNext(Lifecycle.Event.ON_DESTROY)
        super.onCleared()

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: MsgEvent) {
    }

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }
}