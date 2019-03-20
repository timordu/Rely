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

package com.android.rely.common.listview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.BaseAdapter

/**
 * Created by dugang on 2017/9/3. Adapter基类
 */
@Suppress("unused")
abstract class BaseAdapter<T>(var context: Context) : BaseAdapter() {
    protected val mInflater: LayoutInflater by lazy { LayoutInflater.from(context) }
    protected open val mList: MutableList<T> by lazy { mutableListOf<T>() }

    override fun getItem(p0: Int): T = mList[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getCount(): Int = mList.size

    open fun updateData(list: List<T>, loadMore: Boolean = false) {
        if (!loadMore) mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    abstract class ViewHolder(val itemView: View) {

        abstract fun initData(position: Int)
    }
}