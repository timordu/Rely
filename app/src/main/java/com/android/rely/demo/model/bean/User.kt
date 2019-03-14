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

package com.android.rely.demo.model.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Created by dugang on 2018/8/8.
 */
@Parcelize
data class User(var userName: String, var sex: Int, var password: String) : Parcelable

data class DownloadBean(val url: String, var downloadId: Long = 0)

data class UploadBean(val path: String, var uploadId: Long = 0)
