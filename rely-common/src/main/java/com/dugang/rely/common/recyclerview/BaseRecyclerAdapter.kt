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

package com.dugang.rely.common.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by dugang on 2017/12/4.RecyclerView.Adapter
 */
@Suppress("unused")
abstract class BaseRecyclerAdapter<T, K : BaseRecyclerAdapter.ViewHolder>(var context: Context) : RecyclerView.Adapter<K>() {
    val mInflater: LayoutInflater by lazy { LayoutInflater.from(context) }
    val mList = mutableListOf<T>()

    private var emptyView: View? = null
    private var onItemClickListener: ((view: View, position: Int) -> Unit)? = null
    private var onItemLongClickListener: ((view: View, position: Int) -> Boolean)? = null

    init {
        this.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                emptyView?.visibility = if (mList.size == 0) View.VISIBLE else View.GONE
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                emptyView?.visibility = if (mList.size == 0) View.VISIBLE else View.GONE
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                emptyView?.visibility = if (mList.size == 0) View.VISIBLE else View.GONE
            }
        })
    }

    fun setEmptyView(view: View) {
        this.emptyView = view
    }

    fun setOnItemClickListener(onItemClick: (view: View, position: Int) -> Unit) {
        this.onItemClickListener = onItemClick
    }

    fun setOnItemLongClickListener(onItemLongClick: (view: View, position: Int) -> Boolean) {
        this.onItemLongClickListener = onItemLongClick
    }

    fun addItem(data: T, position: Int = 0) {
        if (position >= mList.size) {
            mList.add(data)
            notifyItemInserted(position)
            notifyItemRangeChanged(position, 1)
        } else {
            mList.add(position, data)
            notifyItemInserted(position)
            notifyItemRangeChanged(position, mList.size - position)
        }
    }

    fun updateItem(position: Int, data: T) {
        if (position >= mList.size) return
        mList[position] = data
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        if (mList.size == 0 || position >= mList.size) return
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size - position)
    }

    open fun updateData(list: List<T>, loadMore: Boolean = false) {
        if (!loadMore) mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    open fun getItem(position: Int): T = mList[position]

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: K, position: Int) {
        holder.initData(position)
        holder.itemView.setOnClickListener { view ->
            onItemClickListener?.invoke(view, position)
        }
        holder.itemView.setOnLongClickListener { view ->
            onItemLongClickListener?.invoke(view, position) ?: false
        }
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun initData(position: Int)
    }
}