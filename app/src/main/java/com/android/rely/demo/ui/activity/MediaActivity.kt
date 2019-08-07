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

import com.dugang.rely.common.extension.showToast
import com.android.rely.demo.R
import com.dugang.rely.base.MediaActivity
import kotlinx.android.synthetic.main.act_media.*

class MediaActivity : MediaActivity() {
    override val layoutResId: Int = R.layout.act_media

    override fun initView() {
        photo.setOnClickListener {
            takePhoto()
        }

        pick_photo.setOnClickListener {
            pickPicture()
        }

        video.setOnClickListener {
            takeVideo()
        }
    }

    override fun takePhotoCallBack(filePath: String) {
        super.takePhotoCallBack(filePath)
        showToast(filePath)
    }

    override fun pickPictureCallBack(filePath: String) {
        super.pickPictureCallBack(filePath)
        showToast(filePath)
    }

    override fun takeVideoCallBack(filePath: String) {
        super.takeVideoCallBack(filePath)
        showToast(filePath)
    }

    override fun initObserve() {
    }
}