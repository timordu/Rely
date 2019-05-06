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

package com.android.rely.demo.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.rely.base.BaseViewModel

/**
 * Created by dugang on 2019-04-21.
 */
class WidgetViewModel : BaseViewModel() {
    val singleImage = MutableLiveData<String>()
    val multiImage = MutableLiveData<ArrayList<String>>()


    override fun onResume() {
        super.onResume()
        singleImage.postValue("http://pic37.nipic.com/20140113/8800276_184927469000_2.png")
        multiImage.postValue(arrayListOf(
                "http://pic29.nipic.com/20130517/9252150_140653449378_2.jpg",
                "http://k.zol-img.com.cn/sjbbs/7692/a7691515_s.jpg",
                "http://pic32.nipic.com/20130823/13339320_183302468194_2.jpg",
                "http://pic40.nipic.com/20140412/18428321_144447597175_2.jpg",
                "http://pic37.nipic.com/20140110/17563091_221827492154_2.jpg",
                "http://pic25.nipic.com/20121112/9252150_150552938000_2.jpg",
                "http://img3.redocn.com/tupian/20150312/haixinghezhenzhubeikeshiliangbeijing_3937174.jpg",
                "http://img.redocn.com/sheying/20140731/qinghaihuyuanjing_2820969.jpg",
                "http://pic53.nipic.com/file/20141115/9448607_175255450000_2.jpg"
        ))
    }

}