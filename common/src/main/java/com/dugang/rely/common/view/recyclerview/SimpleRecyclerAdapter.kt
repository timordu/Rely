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

package com.dugang.rely.common.view.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * @description RecyclerView.SimpleRecyclerAdapter
 *
 * @author dugang.
 * @email timor.du@hotmail.com
 * @date  2019/8/7 17:15
 */
class SimpleRecyclerAdapter<T>(context: Context,
                               @LayoutRes val layoutRes: Int,
                               val callBack: (view: View, data: T) -> Unit) : BaseRecyclerAdapter<T, BaseRecyclerAdapter.ViewHolder>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(layoutRes, parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(view: View) : BaseRecyclerAdapter.ViewHolder(view) {
        override fun initData(position: Int) {
            callBack.invoke(itemView, getItem(position))
        }
    }
}