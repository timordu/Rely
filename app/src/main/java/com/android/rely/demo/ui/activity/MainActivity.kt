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
import com.android.rely.ext.skipToActivity
import com.android.rely.mvvm.base.BaseActivity
import com.android.rely.mvvm.ext.initToolBar
import kotlinx.android.synthetic.main.act_main.*

class MainActivity : BaseActivity() {
    override val layoutResId: Int = R.layout.act_main


    override fun initView() {
        initToolBar("功能测试")

        fingerprint.setOnClickListener {
            skipToActivity(FingerprintActivity::class.java)
        }
        widget.setOnClickListener {
            skipToActivity(WidgetActivity::class.java)
        }
    }


    override fun initObserve() {}
}
