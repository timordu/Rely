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

import android.content.Intent
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.dugang.rely.common.view.listview.SimpleAdapter
import com.dugang.rely.common.extension.showToast
import com.android.rely.demo.R
import com.android.rely.demo.ui.activity.widget.FingerprintActivity
import com.android.rely.demo.ui.activity.widget.SideIndexBarDemoActivity
import com.android.rely.demo.ui.parent.MyBaseActivity
import com.android.rely.demo.ui.viewmodel.WidgetViewModel
import com.dugang.rely.extension.loadImage
import com.dugang.rely.common.extension.skipToActivity
import com.dugang.rely.common.extension.smoothSwitchScreen
import com.dugang.rely.common.extension.initToolBar
import com.dugang.rely.common.view.setOnSeekBarChangeListener
import com.dugang.rely.widget.datetime.DateTimePicker
import com.dugang.rely.widget.file_explorer.FileExplorer
import com.wanglu.photoviewerlibrary.PhotoViewer
import kotlinx.android.synthetic.main.act_widget.*
import kotlinx.android.synthetic.main.item_widget.view.*


class WidgetActivity : MyBaseActivity() {
    override val layoutResId: Int = R.layout.act_widget

    private val viewModel: WidgetViewModel by lazy { getViewModel<WidgetViewModel>() }

    override fun initView() {
        smoothSwitchScreen()
        initToolBar("自定义组件测试", R.mipmap.icon_back)

        fingerprint.setOnClickListener {
            skipToActivity<FingerprintActivity>()
        }

        seekBar.setOnSeekBarChangeListener {
            onProgressChanged { _, progress, _ ->
                numberProgressBar.setProgress(progress)
            }
        }


        date.setOnClickListener {
            DateTimePicker.selectDate(mContext) {
                showToast(it)
            }
        }

        time.setOnClickListener {
            DateTimePicker.selectTime(mContext) {
                showToast(it)
            }
        }

        datetime.setOnClickListener {
            DateTimePicker.selectDateTime(mContext) {
                showToast(it)
            }
        }


        sideIndexBar.setOnClickListener {
            skipToActivity<SideIndexBarDemoActivity>()
        }

        file_explorer.setOnClickListener {
            FileExplorer.open(this)
        }
        file_explorer_multi.setOnClickListener {
            FileExplorer.open(this, false)
        }
    }

    override fun initObserve() {
        viewModel.multiImage.observe(this, Observer { urlList ->
            val adapter = SimpleAdapter(this, R.layout.item_widget, urlList) { view, data ->
                view.imageView.loadImage(data)
            }
            multi_image.adapter = adapter
            multi_image.setOnItemClickListener { _, _, position, _ ->
                PhotoViewer.setData(urlList)
                    .setCurrentPage(position)
                    .setImgContainer(multi_image)
                    .setShowImageViewInterface(object :PhotoViewer.ShowImageViewInterface{
                        override fun show(iv: ImageView, url: String) {
                            iv.loadImage(url)
                        }
                    })
                    .start(this)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val list = FileExplorer.onActivityResult(requestCode, resultCode, data)
        list?.forEach {
            Log.d("file_explorer", it.absolutePath)
        }
    }


}