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

import com.android.rely.demo.R
import com.android.rely.demo.ui.parent.MyBaseActivity
import kotlinx.android.synthetic.main.act_side_index_bar.*

class SideIndexBarDemoActivity : MyBaseActivity() {
    override val layoutResId: Int = R.layout.act_side_index_bar

    override fun initView() {
        sideIndexBar.setTextDialog(textView)
    }

    override fun initObserve() {

    }
}