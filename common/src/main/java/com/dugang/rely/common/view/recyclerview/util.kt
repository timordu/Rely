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

import androidx.recyclerview.widget.*
import java.util.*

/**
 * @description RecyclerView的扩展函数
 *
 * @author dugang.
 * @email timor.du@hotmail.com
 * @date  2019/8/7 17:15
 */
fun RecyclerView.setLinearLayoutManager(@RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL, reverseLayout: Boolean = false) {
    layoutManager = LinearLayoutManager(context, orientation, reverseLayout)
}

fun RecyclerView.setGridLayoutManager(span: Int, @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL, reverseLayout: Boolean = false) {
    layoutManager = GridLayoutManager(context, span, orientation, reverseLayout)
}

fun RecyclerView.setStaggeredGridLayoutManager(span: Int, @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL) {
    layoutManager = StaggeredGridLayoutManager(span, orientation)
}

fun RecyclerView.addItemTouchHelper(adapter: BaseRecyclerAdapter<*, *>, swipeEnable: Boolean = false) {
    ItemTouchHelper(object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val swipeFlags = if (swipeEnable) ItemTouchHelper.RIGHT else 0
            val dragFlags = when (recyclerView.layoutManager) {
                is StaggeredGridLayoutManager ->
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                is GridLayoutManager ->
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                else ->
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN
            }
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            if (viewHolder.itemViewType == target.itemViewType) {
                Collections.swap(adapter.mList, viewHolder.adapterPosition, target.adapterPosition)
                adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            adapter.removeItem(viewHolder.adapterPosition)
        }
    }).attachToRecyclerView(this)
}