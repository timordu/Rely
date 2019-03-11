/*
 *      Copyright (c) 2017-2019 dugang
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 */

package com.android.rely.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.annotation.NonNull
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.android.rely.RelyConfig
import com.android.rely.bean.MsgEvent
import com.android.rely.ext.showToast
import com.android.rely.widget.LoadingDialog
import com.trello.rxlifecycle3.LifecycleProvider
import com.trello.rxlifecycle3.LifecycleTransformer
import com.trello.rxlifecycle3.RxLifecycle
import com.trello.rxlifecycle3.android.FragmentEvent
import com.trello.rxlifecycle3.android.RxLifecycleAndroid
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by dugang on 2017/8/21. Fragment基类
 */
@Suppress("OverridingDeprecatedMember", "DEPRECATION")
abstract class BaseFragment : Fragment(), LifecycleProvider<FragmentEvent>,Toolbar.OnMenuItemClickListener {
    private val lifecycleSubject = BehaviorSubject.create<FragmentEvent>()

    protected val mContext: Context get() = activity as Context
    protected val loadingDialog: LoadingDialog by lazy { LoadingDialog(mContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleSubject.onNext(FragmentEvent.CREATE)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(layoutResId, container, false)

    protected abstract val layoutResId: Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW)
        initView(view, savedInstanceState)
    }

    abstract fun initView(view: View, savedInstanceState: Bundle?)

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        return false
    }

    @NonNull
    @CheckResult
    override fun lifecycle(): Observable<FragmentEvent> {
        return lifecycleSubject.hide()
    }

    @NonNull
    @CheckResult
    override fun <T> bindUntilEvent(@NonNull event: FragmentEvent): LifecycleTransformer<T> {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event)
    }

    @NonNull
    @CheckResult
    override fun <T> bindToLifecycle(): LifecycleTransformer<T> {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject)
    }

    @CallSuper
    override fun onAttach(activity: android.app.Activity) {
        super.onAttach(activity)
        lifecycleSubject.onNext(FragmentEvent.ATTACH)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(FragmentEvent.START)
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(FragmentEvent.RESUME)
        EventBus.getDefault().register(this)
    }

    @CallSuper
    override fun onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE)
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    @CallSuper
    override fun onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP)
        super.onStop()
    }

    @CallSuper
    override fun onDestroyView() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW)
        super.onDestroyView()
    }

    @CallSuper
    override fun onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY)
        super.onDestroy()
    }

    @CallSuper
    override fun onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH)
        super.onDetach()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: MsgEvent) {
        if (event.code == RelyConfig.NET_CODE_ERROR) showToast("${event.msg}")
    }
}