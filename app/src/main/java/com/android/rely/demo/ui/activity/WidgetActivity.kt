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

import com.android.rely.common.setOnSeekBarChangeListener
import com.android.rely.common.showToast
import com.android.rely.demo.R
import com.android.rely.mvvm.base.BaseActivity
import com.android.rely.mvvm.ext.initToolBar
import com.android.rely.widget.datetime.DateTimePicker
import kotlinx.android.synthetic.main.act_widget.*
import kotlinx.android.synthetic.main.part_datetime.*
import kotlinx.android.synthetic.main.part_number_progressbar.*


class WidgetActivity : BaseActivity() {
    override val layoutResId: Int = R.layout.act_widget

    override fun initView() {
        initToolBar("自定义组件测试", R.mipmap.icon_back)

        seekBar.setOnSeekBarChangeListener {
            onProgressChanged { _, progress, _ ->
                numberProgressBar.setProgress(progress)
            }
        }


        date.setOnClickListener {
            DateTimePicker.selectDate(mContext){
                showToast(it)
            }
        }

        time.setOnClickListener {
            DateTimePicker.selectTime(mContext){
                showToast(it)
            }
        }

        datetime.setOnClickListener {
            DateTimePicker.selectDateTime(mContext){
                showToast(it)
            }
        }

    }

    override fun initObserve() {

    }


}