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

package com.dugang.rely.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dugang.rely.eventbus.MsgEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @description Activity基类
 *
 * @author dugang.
 * @email timor.du@hotmail.com
 * @date  2019/8/7 17:15
 */
abstract class BaseActivity : AppCompatActivity(), LifecycleOwner {
    open val mContext get() = this

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)
        initView()
        initObserve()
    }

    abstract val layoutResId: Int

    inline fun <reified VM : BaseViewModel> getViewModel(): VM {
        return ViewModelProviders.of(this).get(VM::class.java).apply {
            isShowLoading.observe(this@BaseActivity, Observer {
                if (it) showLoadingDialog() else dismissLoadingDialog()
            })
            lifecycle.addObserver(this)
        }
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


    open fun showLoadingDialog() {

    }

    open fun dismissLoadingDialog() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: MsgEvent) {

    }
}


