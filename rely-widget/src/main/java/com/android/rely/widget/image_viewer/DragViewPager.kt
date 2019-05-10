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

package com.android.rely.widget.image_viewer

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import com.nineoldandroids.view.ViewHelper


/**
 * Created by dugang on 2018/10/17.可下拉的ViewPager
 */
class DragViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    companion object {
        private const val DRAG_PX = 50
        private const val STATUS_NORMAL = 0
        private const val STATUS_MOVING = 1
        private const val STATUS_RESET = 2
    }

    private val screenHeight: Int

    private var currentPageStatus = SCROLL_STATE_IDLE
    private var currentStatus = STATUS_NORMAL
    private var mDownX: Float = 0F
    private var mDownY: Float = 0F
    private var onReleaseListener: (() -> Unit)? = null


    init {
        setBackgroundColor(Color.BLACK)
        addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                currentPageStatus = state
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        screenHeight = outMetrics.heightPixels
    }


    fun setOnReleaseListener(onReleaseListener: () -> Unit) {
        this.onReleaseListener = onReleaseListener
    }

    /**
     * 拦截ViewPager的下滑事件
     */
    @Suppress("DEPRECATION")
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = ev.x
                mDownY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = Math.abs(ev.rawX - mDownX)
                val deltaY = ev.rawY - mDownY
                if (DRAG_PX in deltaX..deltaY) return true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = ev.rawX
                mDownY = ev.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = ev.rawY - mDownY
                if (currentPageStatus != SCROLL_STATE_DRAGGING) {
                    if (deltaY > DRAG_PX || currentStatus == STATUS_MOVING) {
                        moveView(ev.rawX, ev.rawY - DRAG_PX)
                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (currentStatus != STATUS_MOVING)
                    return super.onTouchEvent(ev)
                val mUpX = ev.rawX
                val mUpY = ev.rawY
                //下滑距离超过屏幕1/4退出当前 Activity
                if (Math.abs(mUpY - mDownY) > screenHeight / 4) {
                    onReleaseListener?.invoke()
                }
                //还原原来图片的大小和ViewPager的背景色
                else {
                    resetViewAndBackground(mUpX, mUpY - DRAG_PX)
                }
            }
        }
        return super.onTouchEvent(ev)
    }


    /**
     * 移动缩小View,修改ViewPager的背景色
     */
    private fun moveView(rawX: Float, rawY: Float) {
        currentStatus = STATUS_MOVING

        val currentShowView = (adapter as ImagePreviewAdapter).getItem(currentItem)
        val deltaX = rawX - mDownX
        val deltaY = rawY - mDownY
        var scale = 1f
        var alphaPercent = 1f
        if (deltaY > 0) {
            scale = 1 - Math.abs(deltaY) / screenHeight
            alphaPercent = 1 - Math.abs(deltaY) / (screenHeight / 2)
        }
        ViewHelper.setTranslationX(currentShowView, deltaX)
        ViewHelper.setTranslationY(currentShowView, deltaY)

        scale = Math.min(Math.max(scale, 0.3f), 1f)
        ViewHelper.setScaleX(currentShowView, scale)
        ViewHelper.setScaleY(currentShowView, scale)

        val percent = Math.min(1f, Math.max(0f, alphaPercent))
        setBackgroundColor(Color.argb((percent * 255).toInt(), 0, 0, 0))
    }

    /**
     * 还原View的大小和位置,还原ViewPager的背景色
     */
    private fun resetViewAndBackground(mUpX: Float, mUpY: Float) {
        currentStatus = STATUS_RESET

        //还原X方向
        if (mUpX != mDownX) {
            val valueAnimator = ValueAnimator.ofFloat(mUpX, mDownX)
            valueAnimator.duration = 300
            valueAnimator.addUpdateListener { animation ->
                val mX = animation.animatedValue as Float
                val percent = (mX - mDownX) / (mUpX - mDownX)
                val mY = percent * (mUpY - mDownY) + mDownY
                moveView(mX, mY)
                if (mX == mDownX) {
                    mDownY = 0f
                    mDownX = 0f
                    currentStatus = STATUS_NORMAL
                }
            }
            valueAnimator.start()
        }
        //还原Y方向
        else if (mUpY != mDownY) {
            val valueAnimator = ValueAnimator.ofFloat(mUpY, mDownY)
            valueAnimator.duration = 300
            valueAnimator.addUpdateListener { animation ->
                val mY = animation.animatedValue as Float
                val percent = (mY - mDownY) / (mUpY - mDownY)
                val mX = percent * (mUpX - mDownX) + mDownX
                moveView(mX, mY)
                if (mY == mDownY) {
                    mDownY = 0f
                    mDownX = 0f
                    currentStatus = STATUS_NORMAL
                }
            }
            valueAnimator.start()
        }
    }
}