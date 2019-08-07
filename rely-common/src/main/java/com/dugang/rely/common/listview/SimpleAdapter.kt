/*
 *      Copyright (c) 2017-2019 dugang
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 */

package com.dugang.rely.common.listview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.LayoutRes

/**
 * Created by dugang on 2017/9/3. Adapter简写类
 */
@Suppress("unused")
class SimpleAdapter<T>(private val context: Context,
                       @LayoutRes private val layoutId: Int,
                       private val list: List<T>, val callBack: (view: View, data: T) -> Unit) : BaseAdapter() {

    override fun getItem(p0: Int): T = list[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getCount(): Int = list.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutId, parent, false)
        if (view.tag == null) view.tag = ViewHolder(view)
        (view.tag as SimpleAdapter<*>.ViewHolder).initData(position)
        return view
    }

    private inner class ViewHolder(val itemView: View) {

        fun initData(position: Int) {
            callBack.invoke(itemView, getItem(position))
        }
    }
}
