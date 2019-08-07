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
package com.dugang.rely.widget.progressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import com.dugang.rely.common.extension.dp2px
import com.dugang.rely.common.extension.sp2px
import com.android.rely.widget.R

/**
 * Created by dugang on 2018/12/14.数字进度条
 */
@Suppress("unused")
class NumberProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    //进度条最大值
    private var max = 100
    //进度条当前值
    private var progress = 0
    //进度条参数
    private var mReachedBarHeight: Float
    private var mReachedBarColor: Int
    private var mReachedBarPaint: Paint
    private val mReachedRectF = RectF(0f, 0f, 0f, 0f)
    private var mDrawReachedBar = true

    private var mUnreachedBarHeight: Float
    private var mUnreachedBarColor: Int
    private var mUnreachedBarPaint: Paint
    private val mUnreachedRectF = RectF(0f, 0f, 0f, 0f)
    private var mDrawUnreachedBar = true

    //进度文本参数
    private var mTextColor: Int
    private var mTextSize: Float
    private val mTextOffset: Float
    private var mPrefix: String
    private var mSuffix: String

    private var mTextPaint: Paint
    private var mDrawTextWidth: Float = 0f
    private var mDrawTextStart: Float = 0f
    private var mDrawTextEnd: Float = 0f
    private var mCurrentDrawText: String? = null


    private var mListener: ((current: Int, max: Int) -> Unit)? = null


    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.NumberProgressBar, defStyleAttr, 0)

        max = attributes.getInt(R.styleable.NumberProgressBar_max, 100)
        progress = attributes.getInt(R.styleable.NumberProgressBar_progress, 0)

        mReachedBarHeight = attributes.getDimension(R.styleable.NumberProgressBar_reached_bar_height, context.dp2px(1.5f))
        mReachedBarColor = attributes.getColor(R.styleable.NumberProgressBar_reached_color, Color.parseColor("#3498DB"))
        mReachedBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = mReachedBarColor }

        mUnreachedBarHeight = attributes.getDimension(R.styleable.NumberProgressBar_unreached_bar_height, context.dp2px(0.75f))
        mUnreachedBarColor = attributes.getColor(R.styleable.NumberProgressBar_unreached_color, Color.parseColor("#CCCCCC"))
        mUnreachedBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = mUnreachedBarColor }

        mTextColor = attributes.getColor(R.styleable.NumberProgressBar_text_color, Color.parseColor("#3498DB"))
        mTextSize = attributes.getDimension(R.styleable.NumberProgressBar_text_size, context.sp2px(10f))
        mTextOffset = attributes.getDimension(R.styleable.NumberProgressBar_text_offset, context.dp2px(3f))

        mPrefix = attributes.getString(R.styleable.NumberProgressBar_text_prefix) ?: ""
        mSuffix = attributes.getString(R.styleable.NumberProgressBar_text_suffix) ?: "%"


        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mTextColor
            textSize = mTextSize
        }
        attributes.recycle()
    }


    override fun getSuggestedMinimumWidth(): Int {
        return mTextSize.toInt()
    }

    override fun getSuggestedMinimumHeight(): Int {
        return Math.max(mTextSize.toInt(), Math.max(mReachedBarHeight.toInt(), mUnreachedBarHeight.toInt()))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false))
    }

    private fun measure(measureSpec: Int, isWidth: Boolean): Int {
        var result: Int
        val mode = View.MeasureSpec.getMode(measureSpec)
        val size = View.MeasureSpec.getSize(measureSpec)
        val padding = if (isWidth) paddingLeft + paddingRight else paddingTop + paddingBottom
        if (mode == View.MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = if (isWidth) suggestedMinimumWidth else suggestedMinimumHeight
            result += padding
            if (mode == View.MeasureSpec.AT_MOST) {
                result = if (isWidth) {
                    Math.max(result, size)
                } else {
                    Math.min(result, size)
                }
            }
        }
        return result
    }


    override fun onDraw(canvas: Canvas) {
        calculateDrawRectF()

        if (mDrawReachedBar) canvas.drawRect(mReachedRectF, mReachedBarPaint)

        if (mDrawUnreachedBar) canvas.drawRect(mUnreachedRectF, mUnreachedBarPaint)

        canvas.drawText(mCurrentDrawText!!, mDrawTextStart, mDrawTextEnd, mTextPaint)
    }

    private fun calculateDrawRectF() {
        mCurrentDrawText = String.format("%d", progress * 100 / max)
        mCurrentDrawText = mPrefix + mCurrentDrawText + mSuffix
        mDrawTextWidth = mTextPaint.measureText(mCurrentDrawText)

        if (progress == 0) {
            mDrawReachedBar = false
            mDrawTextStart = paddingLeft.toFloat()
        } else {
            mDrawReachedBar = true
            mReachedRectF.left = paddingLeft.toFloat()
            mReachedRectF.top = height / 2.0f - mReachedBarHeight / 2.0f
            mReachedRectF.right = (width - paddingLeft - paddingRight) / (max * 1.0f) * progress - mTextOffset + paddingLeft
            mReachedRectF.bottom = height / 2.0f + mReachedBarHeight / 2.0f
            mDrawTextStart = mReachedRectF.right + mTextOffset
        }

        mDrawTextEnd = (height / 2.0f - (mTextPaint.descent() + mTextPaint.ascent()) / 2.0f).toInt().toFloat()

        if (mDrawTextStart + mDrawTextWidth >= width - paddingRight) {
            mDrawTextStart = width.toFloat() - paddingRight.toFloat() - mDrawTextWidth
            mReachedRectF.right = mDrawTextStart - mTextOffset
        }

        val unreachedBarStart = mDrawTextStart + mDrawTextWidth + mTextOffset
        if (unreachedBarStart >= width - paddingRight) {
            mDrawUnreachedBar = false
        } else {
            mDrawUnreachedBar = true
            mUnreachedRectF.left = unreachedBarStart
            mUnreachedRectF.right = (width - paddingRight).toFloat()
            mUnreachedRectF.top = height / 2.0f + -mUnreachedBarHeight / 2.0f
            mUnreachedRectF.bottom = height / 2.0f + mUnreachedBarHeight / 2.0f
        }
    }

    companion object {
        private const val INSTANCE_STATE = "saved_instance"
        private const val INSTANCE_TEXT_COLOR = "text_color"
        private const val INSTANCE_TEXT_SIZE = "text_size"
        private const val INSTANCE_REACHED_BAR_HEIGHT = "reached_bar_height"
        private const val INSTANCE_REACHED_BAR_COLOR = "reached_bar_color"
        private const val INSTANCE_UNREACHED_BAR_HEIGHT = "unreached_bar_height"
        private const val INSTANCE_UNREACHED_BAR_COLOR = "unreached_bar_color"
        private const val INSTANCE_MAX = "max"
        private const val INSTANCE_PROGRESS = "progress"
        private const val INSTANCE_SUFFIX = "suffix"
        private const val INSTANCE_PREFIX = "prefix"
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState())

        bundle.putInt(INSTANCE_TEXT_COLOR, mTextColor)
        bundle.putFloat(INSTANCE_TEXT_SIZE, mTextSize)
        bundle.putString(INSTANCE_SUFFIX, mSuffix)
        bundle.putString(INSTANCE_PREFIX, mPrefix)

        bundle.putInt(INSTANCE_REACHED_BAR_COLOR, mReachedBarColor)
        bundle.putFloat(INSTANCE_REACHED_BAR_HEIGHT, mReachedBarHeight)

        bundle.putInt(INSTANCE_UNREACHED_BAR_COLOR, mUnreachedBarColor)
        bundle.putFloat(INSTANCE_UNREACHED_BAR_HEIGHT, mUnreachedBarHeight)

        bundle.putInt(INSTANCE_MAX, max)
        bundle.putInt(INSTANCE_PROGRESS, progress)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        mTextColor = bundle.getInt(INSTANCE_TEXT_COLOR)
        mTextSize = bundle.getFloat(INSTANCE_TEXT_SIZE)
        mReachedBarHeight = bundle.getFloat(INSTANCE_REACHED_BAR_HEIGHT)
        mUnreachedBarHeight = bundle.getFloat(INSTANCE_UNREACHED_BAR_HEIGHT)
        mReachedBarColor = bundle.getInt(INSTANCE_REACHED_BAR_COLOR)
        mUnreachedBarColor = bundle.getInt(INSTANCE_UNREACHED_BAR_COLOR)

        mReachedBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = mReachedBarColor }

        mUnreachedBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = mUnreachedBarColor }

        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mTextColor
            textSize = mTextSize
        }

        max = bundle.getInt(INSTANCE_MAX)
        progress = bundle.getInt(INSTANCE_PROGRESS)
        mPrefix = bundle.getString(INSTANCE_PREFIX) ?: ""
        mSuffix = bundle.getString(INSTANCE_SUFFIX) ?: "%"

        super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE))
    }

    fun setProgress(progress: Int) {
        if (progress in 0..max) {
            this.progress = progress
            invalidate()
            mListener?.invoke(progress, max)
        }
    }

    fun setOnProgressBarListener(listener: (current: Int, max: Int) -> Unit) {
        mListener = listener
    }
}
