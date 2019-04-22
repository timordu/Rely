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

package com.android.rely.demo.ui.activity

import com.android.rely.demo.R
import com.android.rely.demo.ui.parent.MyBaseActivity
import com.android.rely.demo.ui.viewmodel.TestViewModel
import com.android.rely.ext.skipToActivity
import com.android.rely.mvvm.ext.initToolBar
import com.android.rely.mvvm.widget.LoadingDialog
import kotlinx.android.synthetic.main.act_main.*

class MainActivity : MyBaseActivity() {
    override val layoutResId: Int = R.layout.act_main

    private val testViewModel: TestViewModel by lazy { getViewModel(TestViewModel::class.java) }

    override fun initView() {
        initToolBar("功能测试")
        network_test.setOnClickListener {
            //传入自定义的LoadingDialog,可以手动取消请求
            testViewModel.login("", "", LoadingDialog(mContext))
            //通过MyBaseActivity中定义的show和dismiss方法来控制全局的LoadingDialog
//            testViewModel.login("","")
        }

        fingerprint.setOnClickListener {
            skipToActivity(FingerprintActivity::class.java)
        }
        widget.setOnClickListener {
            skipToActivity(WidgetActivity::class.java)
        }
    }


    override fun initObserve() {}


}
