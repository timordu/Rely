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

package com.android.rely.ext

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


fun ImageView.loadImage(path: Any, options: RequestOptions = RequestOptions()) {
    Glide.with(context).load(path).apply(options).into(this)
}

fun ImageView.loadCircleImage(path: Any, options: RequestOptions = RequestOptions()) {
    Glide.with(context).load(path).apply(RequestOptions.circleCropTransform()).apply(options).into(this)

}

fun ImageView.loadRoundCornerImage(path: Any, radius: Int = 20, options: RequestOptions = RequestOptions()) {
    Glide.with(context).load(path).apply(RequestOptions.bitmapTransform(RoundedCorners(radius))).apply(options).into(this)
}

fun ImageView.loadClear() {
    Glide.with(context).clear(this)
}