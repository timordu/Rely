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

package com.android.rely.common.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.rely.common.R
import com.android.rely.common.getCompatColor
import com.android.rely.common.getDimensionPixelSize

/**
 * create by dugang on 2018/12/13. RecyclerView通用分割线
 */
@Suppress("unused")
class RecyclerItemDecoration(context: Context,
                             @ColorRes color: Int = R.color.transparent,
                             @DimenRes hSpan: Int = R.dimen.dp_0,
                             @DimenRes vSpan: Int = R.dimen.dp_0) : RecyclerView.ItemDecoration() {

    private val mDivider = ColorDrawable(context.getCompatColor(color))
    private val mHorizonSpan = context.getDimensionPixelSize(hSpan)
    private val mVerticalSpan = context.getDimensionPixelSize(vSpan)

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        drawHorizontal(c, parent)
        drawVertical(c, parent)
    }

    private fun getSpanCount(parent: RecyclerView): Int = when {
        parent.layoutManager is GridLayoutManager -> (parent.layoutManager as GridLayoutManager).spanCount
        parent.layoutManager is StaggeredGridLayoutManager -> (parent.layoutManager as StaggeredGridLayoutManager).spanCount
        else -> 1
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(mVerticalSpan / 2, mHorizonSpan / 2, mVerticalSpan / 2, mHorizonSpan / 2)
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if ((parent.getChildViewHolder(child).adapterPosition + 1) % getSpanCount(parent) == 0) continue
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin + mHorizonSpan
            val left = child.right + params.rightMargin
            var right = left + mVerticalSpan
            if (i == childCount - 1) right -= mVerticalSpan
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (isLastRow(parent, i, getSpanCount(parent), childCount)) continue
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.left - params.leftMargin
            val right = child.right + params.rightMargin
            val top = child.bottom + params.bottomMargin
            val bottom = top + mHorizonSpan
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    private fun isLastRow(parent: RecyclerView, pos: Int, spanCount: Int, childCount: Int): Boolean {
        val orientation = when (parent.layoutManager) {
            is GridLayoutManager ->
                (parent.layoutManager as GridLayoutManager).orientation
            is StaggeredGridLayoutManager ->
                (parent.layoutManager as StaggeredGridLayoutManager).orientation
            else ->
                (parent.layoutManager as LinearLayoutManager).orientation
        }
        if (orientation == RecyclerView.VERTICAL) {
            return getResult(pos, spanCount, childCount)
        } else {
            if ((pos + 1) % spanCount == 0) return true
        }
        return false
    }

    private fun getResult(pos: Int, spanCount: Int, childCount: Int): Boolean {
        val remainCount = childCount % spanCount
        return pos >= childCount - if (remainCount == 0) spanCount else remainCount
    }
}