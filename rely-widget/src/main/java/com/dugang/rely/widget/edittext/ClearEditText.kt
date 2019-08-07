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

package com.dugang.rely.widget.edittext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import com.dugang.rely.common.extension.getCompatDrawable
import com.android.rely.widget.R

/**
 * 如果需要使用自己的删除图标,可在项目目录中存放名为"icon_delete"图片即可.
 */
@Suppress("unused")
class ClearEditText(context: Context, attrs: AttributeSet? = null) : EditText(context, attrs), View.OnFocusChangeListener, TextWatcher {

    private var mClearDrawable: Drawable? = context.getCompatDrawable(R.mipmap.icon_delete)
    private var hasFocus: Boolean = false

    init {
        mClearDrawable?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
        }
        onFocusChangeListener = this
        addTextChangedListener(this)
        setDrawableVisible(false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP)
            if (compoundDrawablesRelative[2] != null)
                if (event.x > width - totalPaddingRight && event.x < width - paddingRight)
                    setText("")
        return super.onTouchEvent(event)
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        this.hasFocus = hasFocus
        setDrawableVisible(hasFocus && text.isNotEmpty())
    }

    override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        setDrawableVisible(hasFocus && s.isNotEmpty())
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable) {}

    private fun setDrawableVisible(visible: Boolean) {
        val right = if (visible) mClearDrawable else null
        setCompoundDrawablesRelative(compoundDrawablesRelative[0], compoundDrawablesRelative[1], right, compoundDrawablesRelative[3])
    }

}