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

package com.android.rely.widget.image_viewer

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.android.rely.widget.R
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

/**
 * Created by dugang on 2018/10/16.
 */
class ImagePreviewAdapter(private var list: MutableList<PhotoView>) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun getCount(): Int = list.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = list[position].apply {
            Glide.with(context).load(getTag(R.id.tag_id)).into(this)
        }
        container.addView(photoView)
        return photoView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(list[position])
    }

    override fun getItemPosition(`object`: Any): Int = POSITION_NONE

    fun getItem(position: Int): PhotoView = list[position]
}