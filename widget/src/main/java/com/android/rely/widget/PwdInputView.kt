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

package com.android.rely.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.RectF
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.widget.EditText
import com.android.rely.common.getCompatColor
import com.android.rely.common.getDimension

/**
 * Created by dugang on 2018/12/14.密码和验证码输入控件
 */
//todo 移除内容类型
@Suppress("unused")
class PwdInputView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : EditText(context, attrs, defStyleAttr) {
    private val maxCount: Int
    //内容类型,0-密码 1-数字
    private val contentType: Int
    private val pwdRadius: Float
    //边界类型,0-方框 1-下划线
    private val borderType: Int
    private val borderWidth: Float
    private val borderAngle: Float

    private val borderPaint: Paint
    private val secondBorderPaint: Paint
    private val pwdPaint: Paint
    private val numberPaint: Paint

    private val borderRectF = RectF()
    private var underLineLength: Int = 0
    private var dividerStartX = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var currentPos = 0

    private var onCompletedListener: OnCompletedListener? = null

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.PwdInputView, defStyleAttr, defStyleAttr)
        maxCount = attributes.getInt(R.styleable.PwdInputView_maxCount, 6)
        contentType = attributes.getInt(R.styleable.PwdInputView_contentType, 0)

        borderType = attributes.getInt(R.styleable.PwdInputView_borderType, 0)
        val borderColor = attributes.getColor(R.styleable.PwdInputView_borderColor, context.getCompatColor(R.color.gray))
        val secondBorderColor = attributes.getColor(R.styleable.PwdInputView_secondBorderColor, context.getCompatColor(R.color.gray))
        borderWidth = attributes.getDimension(R.styleable.PwdInputView_borderWidth, context.getDimension(R.dimen.dp_1))
        borderAngle = attributes.getDimension(R.styleable.PwdInputView_borderAngle, 0f)

        pwdRadius = attributes.getDimension(R.styleable.PwdInputView_pwdRadius, context.getDimension(R.dimen.dp_3))
        val pwdColor = attributes.getInt(R.styleable.PwdInputView_pwdColor, context.getCompatColor(R.color.black))
        val numberSize = attributes.getFloat(R.styleable.PwdInputView_numberSize, context.getDimension(R.dimen.sp_14))
        val numberColor = attributes.getColor(R.styleable.PwdInputView_numberColor, context.getCompatColor(R.color.black))
        attributes.recycle()

        borderPaint = Paint(ANTI_ALIAS_FLAG).apply {
            strokeWidth = borderWidth
            style = Paint.Style.STROKE
            color = borderColor
            isAntiAlias = true
        }

        secondBorderPaint = Paint(ANTI_ALIAS_FLAG).apply {
            strokeWidth = borderWidth
            style = Paint.Style.STROKE
            color = secondBorderColor
            isAntiAlias = true
        }

        pwdPaint = Paint(ANTI_ALIAS_FLAG).apply {
            strokeWidth = pwdRadius
            style = Paint.Style.FILL
            color = pwdColor
            isAntiAlias = true
        }
        numberPaint = Paint(ANTI_ALIAS_FLAG).apply {
            textSize = numberSize
            color = numberColor
            isAntiAlias = true
        }

        //可以获取焦点,显示软键盘
        isFocusable = true
        isFocusableInTouchMode = true
        //隐藏光标
        isCursorVisible = false
        //移除复制,选择功能
        setTextIsSelectable(false)
        isLongClickable = false
        //添加长度,类型限制
        filters = arrayOf(InputFilter.LengthFilter(maxCount))
        inputType = InputType.TYPE_CLASS_NUMBER
        //
        setBackgroundColor(context.getCompatColor(R.color.transparent))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width = w
        height = h

        dividerStartX = (w.toFloat() - 2 * borderWidth) / maxCount

        centerX = (w.toFloat() - (maxCount + 1) * borderWidth) / maxCount / 2
        centerY = h.toFloat() / 2

        underLineLength = w / (maxCount + 2)
    }

    override fun onDraw(canvas: Canvas) {
        when (borderType) {
            0 -> {
                borderRectF.set(borderWidth / 2, borderWidth / 2, width.toFloat() - borderWidth / 2, height.toFloat() - borderWidth / 2)
                canvas.drawRoundRect(borderRectF, borderAngle, borderAngle, borderPaint)
                (1 until maxCount).forEach {
                    val startX = dividerStartX * it + borderWidth
                    canvas.drawLine(startX, borderWidth / 2, startX, height.toFloat() - borderWidth / 2, borderPaint)
                }
            }
            1 -> {
                (0 until maxCount).forEach {
                    val startX = centerX * (it * 2 + 1) + borderWidth * (it + 1) - underLineLength / 2f
                    val startY = height.toFloat() - borderWidth / 2
                    canvas.drawLine(startX, startY, startX + underLineLength, startY, if (currentPos > it) secondBorderPaint else borderPaint)
                }
            }
            2 -> {
                (0 until maxCount).forEach {
                    val startX = centerX * (it * 2 + 1) + borderWidth * (it + 1) - underLineLength / 2f
                    val startY = centerY - underLineLength / 2
                    borderRectF.set(startX, startY, startX + underLineLength, startY + underLineLength)
                    canvas.drawRoundRect(borderRectF, borderAngle, borderAngle, if (currentPos > it) secondBorderPaint else borderPaint)
                }
            }
        }

        (0 until text.length).forEach {
            when (contentType) {
                0 -> canvas.drawCircle(centerX * (it * 2 + 1) + borderWidth * (it + 1), centerY, pwdRadius, pwdPaint)
                1 -> {
                    val str = text[it].toString()
                    val textStartX = centerX * (it * 2 + 1) + borderWidth * (it + 1) - numberPaint.measureText(str) / 2
                    val fontMetrics = numberPaint.fontMetrics
                    val textStartY = centerY + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2
                    canvas.drawText(text[it].toString(), textStartX, textStartY, numberPaint)
                }
            }
        }
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        currentPos = start + lengthAfter
        invalidate()
        text?.let {
            if (it.length == maxCount) onCompletedListener?.onCompleted(it.toString())
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (selStart == selEnd) setSelection(text.length)
    }

    fun setOnCompletedListener(onCompletedListener: OnCompletedListener) {
        this.onCompletedListener = onCompletedListener
    }

    interface OnCompletedListener {
        fun onCompleted(text: String)
    }
}