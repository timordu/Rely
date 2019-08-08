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
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.EditText
import com.dugang.rely.common.extension.getCompatDrawable
import com.dugang.rely.widget.R

/**
 * 如果需要使用自己的图标,可在项目目录中存放名为"icon_eye_open"和"icon_eye_close"图片即可.
 */
@Suppress("unused")
class PasswordEditText(context: Context, attrs: AttributeSet? = null) : EditText(context, attrs) {

    private var isOpen: Boolean = false

    init {
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        setEyeState(isOpen)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP)
            if (event.x > width - totalPaddingRight && event.x < width - paddingRight)
                setEyeState(!isOpen)
        return super.onTouchEvent(event)
    }

    private fun setEyeState(state: Boolean) {
        val drawable = context.getCompatDrawable(if (state) R.mipmap.icon_eye_open else R.mipmap.icon_eye_close)
        drawable?.let {
            it.setBounds(0, 0, it.minimumWidth, it.minimumHeight)
        }
        setCompoundDrawablesRelative(compoundDrawablesRelative[0], compoundDrawablesRelative[1], drawable, compoundDrawablesRelative[3])

        isOpen = state
        transformationMethod = if (state)
            HideReturnsTransformationMethod.getInstance()
        else
            PasswordTransformationMethod.getInstance()
    }
}