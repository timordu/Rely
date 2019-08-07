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

package com.android.rely.demo.ui.activity.widget

import com.dugang.rely.common.extension.getSP
import com.dugang.rely.common.extension.md5
import com.dugang.rely.common.extension.setSP
import com.dugang.rely.common.extension.showToast
import com.android.rely.demo.R
import com.android.rely.demo.ui.parent.MyBaseActivity
import com.dugang.rely.common.extension.initToolBar
import com.dugang.rely.widget.fingerprint.FingerprintManager
import kotlinx.android.synthetic.main.act_fingerprint.*

class FingerprintActivity : MyBaseActivity() {
    override val layoutResId: Int = R.layout.act_fingerprint

    override fun initView() {
        initToolBar("指纹识别", R.mipmap.icon_back)

        open_fingerprint.setOnClickListener {
            FingerprintManager.showAuthDialog(mContext) { success, _ ->
                val open = getSP().getBoolean("open_fingerprint".md5(), false)
                if (success) {
                    open_fingerprint.isChecked = !open
                    setSP("open_fingerprint".md5(), !open)
                } else
                    open_fingerprint.isChecked = open
            }
        }
        open_fingerprint.isChecked = getSP().getBoolean("open_fingerprint".md5(), false)
        fingerprint.setOnClickListener {
            if (!getSP().getBoolean("open_fingerprint".md5(), false)) {
                showToast("请先开启指纹")
                return@setOnClickListener
            }
            FingerprintManager.showAuthDialog(mContext) { success, message ->
                showToast("$success,$message")
            }
        }
    }

    override fun initObserve() {}
}