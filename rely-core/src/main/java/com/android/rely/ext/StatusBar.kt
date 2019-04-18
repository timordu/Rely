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

package com.android.rely.ext

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange


/*
  //获取状态栏高度
  Context.getStatusBarHeight()

  //5.0及以上动态设置页面状态栏颜色,全局配置使用colorPrimaryDark
  Activity.setStatusBarColor(color,percent)
  Activity.setStatusBarColor(color,alpha)
  Activity.setStatusBarColor(color)

  //设置状态栏字体图标颜色
  Activity.setLightMode(darkIcon)
 */

/**
 * 获取状态栏高度
 */
fun Context.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
}

/**
 * 设置状态栏颜色
 * @param  color #RGB
 * @param  percent 0-1f
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setStatusBarColor(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) percent: Float) {
    setStatusBarColor(calculateColor(color, percent))
}

/**
 * 设置状态栏颜色
 * @param  color #RGB
 * @param  alpha 0-255
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setStatusBarColor(@ColorInt color: Int, @IntRange(from = 0, to = 255) alpha: Int) {
    setStatusBarColor(calculateColor(color, alpha))
}

/**
 * 设置状态栏颜色
 * @param  color #ARGB
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setStatusBarColor(@ColorInt color: Int) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = color
}

/**
 * 设置状态栏字体图标颜色
 * @param darkIcon 是否深色
 */
fun Activity.setLightMode(darkIcon: Boolean = true) {
    if (isFlymeV4OrAbove()) {
        setFlymeStatusBarDarkIcon(darkIcon)
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && darkIcon)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        else
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}

fun Activity.setStatusBarFullTransparent() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }
}


private fun Activity.setFlymeStatusBarDarkIcon(darkIcon: Boolean) {
    try {
        val lp = window.attributes
        val darkFlag = WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
        val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
        darkFlag.isAccessible = true
        meizuFlags.isAccessible = true

        val bit = darkFlag.getInt(null)
        var value = meizuFlags.getInt(lp)

        value = if (darkIcon) value or bit else value and bit.inv()
        meizuFlags.setInt(lp, value)
        window.attributes = lp
    } catch (e: Exception) {
        //e.printStackTrace();
    }
}


private fun calculateColor(@ColorInt color: Int, alpha: Int): Int {
    if (alpha == 0) return color

    val a = 1 - alpha / 255f
    val red = ((color shr 16 and 0xff) * a + 0.5).toInt()
    val green = ((color shr 8 and 0xff) * a + 0.5).toInt()
    val blue = ((color and 0xff) * a + 0.5).toInt()
    return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
}

private fun calculateColor(@ColorInt color: Int, percent: Float): Int {
    if (percent == 1.0f) return color

    val a = 1 - percent
    val red = ((color shr 16 and 0xff) * a + 0.5).toInt()
    val green = ((color shr 8 and 0xff) * a + 0.5).toInt()
    val blue = ((color and 0xff) * a + 0.5).toInt()
    return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
}



