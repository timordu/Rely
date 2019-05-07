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

package com.android.rely.demo.ui.activity.conditionskip

import com.android.rely.condition_skip.ConditionSkip
import com.android.rely.demo.Contains
import com.android.rely.demo.R
import com.android.rely.common.closeActivity
import com.android.rely.base.BaseActivity
import com.android.rely.common.initToolBar
import kotlinx.android.synthetic.main.act_login.*

class LoginActivity : BaseActivity() {
    override val layoutResId = R.layout.act_login

    override fun initView() {
        initToolBar("login", R.mipmap.icon_back)
        login.setOnClickListener {
            Contains.isLogin = true
            ConditionSkip.get("skipToConditionSkipActivity")?.doCall()
            closeActivity()
        }
    }

    override fun initObserve() {
    }
}
