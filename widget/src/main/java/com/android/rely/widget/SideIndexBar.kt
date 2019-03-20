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
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.android.rely.common.sp2px

/**
 * Created by dugang on 2017/5/17.侧边栏字母索引
 */
@Suppress("unused")
class SideIndexBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    // 字母列表颜色
    private var mLetterColor: Int
    // 被选中的字母颜色
    private var mSelectedLetterColor: Int
    // 字母字体大小
    private var mLetterSize: Float

    private var mWidth: Int = 0//宽度
    private var mHeight: Int = 0//去除padding后的高度
    private var mChoose = -1// 选中的字母是第几个
    private var mPaint: Paint //画笔


    //显示当前索引字母的对话框
    private var mTextDialog: TextView? = null
    //默认字符
    private var mLetters = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    // 触摸字母改变事件
    private var mLetterChangedListener: OnLetterChangedListener? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SideIndexBar, defStyleAttr, 0)
        mLetterSize = a.getDimension(R.styleable.SideIndexBar_letterSize, context.sp2px(12f))
        mLetterColor = a.getColor(R.styleable.SideIndexBar_letterColor, Color.parseColor("#333333"))
        mSelectedLetterColor = a.getColor(R.styleable.SideIndexBar_selectedLetterColor, Color.parseColor("#999999"))
        a.recycle()

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.DEFAULT_BOLD
            textSize = mLetterSize
            isAntiAlias = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        mHeight = Math.abs(mPaint.fontMetricsInt.top - mPaint.fontMetricsInt.bottom) * mLetters.length + paddingTop + paddingBottom
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas) {
        var i = 0
        val len = mLetters.length
        //单个item的高度
        val itemHeight = mHeight - paddingTop - paddingBottom
        while (i < len) {
            val letter = mLetters.substring(i, i + 1)
            val letterWidth = mPaint.measureText(letter)

            mPaint.color = if (i == mChoose) mSelectedLetterColor else mLetterColor
            // 计算（x,y），默认是该字母的左下角坐标
            val xPos = (mWidth - letterWidth) / 2
            val yPos = Math.abs(mPaint.fontMetricsInt.top).toFloat() + itemHeight / mLetters.length * i + paddingTop
            canvas.drawText(letter, xPos, yPos, mPaint)
            i++
        }
        super.onDraw(canvas)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val oldChoose = mChoose
        mChoose = if (event.y < paddingTop || event.y > mHeight + paddingTop) {
            -1
        } else {
            // 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
            ((event.y - paddingTop) / mHeight * mLetters.length).toInt()
        }
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                mChoose = -1
                mTextDialog?.visibility = View.INVISIBLE
                invalidate()
            }
            else ->
                if (oldChoose != mChoose && mChoose != -1) {
                    mLetterChangedListener?.onChanged(mLetters.substring(mChoose, mChoose + 1))
                    mTextDialog?.text = mLetters.substring(mChoose, mChoose + 1)
                    mTextDialog?.visibility = View.VISIBLE

                    invalidate()
                    return true
                }
        }
        return super.dispatchTouchEvent(event)
    }

    /**
     * 设置要展示的字母
     */
    fun setLetters(letters: String) {
        this.mLetters = letters
        invalidate()
    }

    /**
     * 设置当前选中字母的展示器
     */
    fun setTextDialog(textDialog: TextView) {
        this.mTextDialog = textDialog
    }

    /**
     * 设置监听器
     */
    fun setOnLetterChangedListener(letterChangedListener: OnLetterChangedListener) {
        mLetterChangedListener = letterChangedListener
    }

    /**
     * 选中监听器
     */
    interface OnLetterChangedListener {
        fun onChanged(str: String)
    }
}
