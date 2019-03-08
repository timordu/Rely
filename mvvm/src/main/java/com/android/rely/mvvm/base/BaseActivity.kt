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

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.android.rely.Rely
import com.android.rely.common.showToast
import com.android.rely.eventbus.MsgEvent
import com.android.rely.mvvm.widget.LoadingDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by dugang on 2017/7/26.Activity基类
 */
@Suppress("unused")
abstract class BaseActivity : AppCompatActivity(), LifecycleOwner {
    protected open val mContext get() = this
    protected val mLoadingDialog: LoadingDialog by lazy { LoadingDialog(mContext) }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)
        initView()
    }

    protected abstract val layoutResId: Int

    protected fun <VM : BaseViewModel> initViewModel(clazz: Class<VM>): VM {
        val viewModel = ViewModelProviders.of(this).get(clazz)
        lifecycle.addObserver(viewModel)
        return viewModel
    }


    abstract fun initView()

    abstract fun initObserve()

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }


    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: MsgEvent) {
        if (event.code == Rely.NET_CODE_ERROR) showToast("${event.msg}")
    }

}


