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

package com.dugang.rely.common.extension

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.Surface
import android.view.WindowManager
import androidx.annotation.DimenRes

/**
 * @description 屏幕相关的扩展函数
 *
 * @author dugang.
 * @email timor.du@hotmail.com
 * @date  2019/8/7 17:21
 */

/*
  ---------- 屏幕尺寸及单位转换 ----------
 */
val Context.screenWidth: Int
    get() = this.resources.displayMetrics.widthPixels

val Context.screenHeight: Int
    get() = resources.displayMetrics.heightPixels

val Context.density: Float
    get() = resources.displayMetrics.density

val Context.scaledDensity: Float
    get() = resources.displayMetrics.scaledDensity

fun Context.isTablet(): Boolean =
        resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE

fun Context.dp2px(value: Float): Float = value * density + 0.5f

fun Context.sp2px(value: Float): Float = value * scaledDensity + 0.5f

fun Context.px2dp(px: Int): Int = (px / density + 0.5f).toInt()

fun Context.px2sp(px: Int): Int = (px / scaledDensity + 0.5f).toInt()

fun Context.dimen2px(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)


/**
 * 设置全屏显示
 */
fun Activity.setFullScreen() {
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

/**
 * 退出全屏显示
 */
fun Activity.setNonFullScreen() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

/**
 * 判断是否全屏显示
 */
fun Activity.isFullScreen(): Boolean {
    val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
    return window.attributes.flags and fullScreenFlag == fullScreenFlag
}

/**
 * 设置横屏显示
 */
fun Activity.setScreenLandscape() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

/**
 * 设置竖屏显示
 */
fun Activity.setScreenPortrait() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

/**
 * 获取屏幕方向: 1-竖屏 2-横屏
 */
fun Context.getScreenOrientation(): Int = resources.configuration.orientation

/**
 * 获取屏幕旋转角度
 */
fun Context.getScreenRotation(): Int = when (windowManager.defaultDisplay.rotation) {
    Surface.ROTATION_90 -> 90
    Surface.ROTATION_180 -> 180
    Surface.ROTATION_270 -> 270
    else -> 0
}