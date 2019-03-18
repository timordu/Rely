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

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Surface
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.android.rely.common.hideSoftInput
import com.android.rely.common.windowManager

/**
 * 跳转到指定的Activity
 */
fun Activity.skipToActivity(clazz: Class<out Activity>, bundle: Bundle? = null) {
    val intent = Intent(this, clazz).apply {
        putExtras(Bundle().apply { bundle?.let { putAll(it) } })
    }
    startActivity(intent)
}

/**
 * 带返回的跳转到指定的Activity
 */
fun Activity.skipToActivityForResult(clazz: Class<out Activity>, requestCode: Int, bundle: Bundle? = null) {
    val intent = Intent(this, clazz).apply {
        putExtras(Bundle().apply { bundle?.let { putAll(it) } })
    }
    startActivityForResult(intent, requestCode)
}

/**
 * 跳转到指定的Activity并结束当前Activity
 */
fun Activity.skipToActivityAndFinish(clazz: Class<out Activity>, bundle: Bundle? = null) {
    val intent = Intent(this, clazz).apply {
        putExtras(Bundle().apply { bundle?.let { putAll(it) } })
    }
    startActivity(intent)
    finish()
}

/**
 * 从新线程启动指定的Activity
 */
fun Activity.skipToActivityWithNewTask(clazz: Class<out Activity>, bundle: Bundle? = null) {
    val intent = Intent(this, clazz).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtras(Bundle().apply { bundle?.let { putAll(it) } })
    }
    startActivity(intent)
    finish()
}

/**
 * 关闭界面
 */
fun Activity.closeActivity(bundle: Bundle? = null) {
    bundle?.let { setResult(RESULT_OK, Intent().apply { putExtras(it) }) }
    hideSoftInput()
    finish()
}

/**
 * Fragment事务扩展
 */
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commitAllowingStateLoss()
}

/**
 * 替换Fragment
 */
fun FragmentActivity.replaceFragment(@IdRes layoutId: Int, f: Fragment, bundle: Bundle? = null, tag: String? = null) {
    supportFragmentManager.inTransaction {
        replace(layoutId, f.apply { arguments = bundle }, tag)
    }
}

/**
 * 添加Fragment
 */
fun FragmentActivity.addFragment(@IdRes layoutId: Int, f: Fragment, bundle: Bundle? = null, tag: String? = null) {
    supportFragmentManager.inTransaction {
        add(layoutId, f.apply { arguments = bundle }, tag).addToBackStack(null)
    }
}

/**
 * 隐藏Fragment
 */
fun FragmentActivity.hideFragment(f: Fragment) {
    supportFragmentManager.inTransaction { hide(f) }
}

/**
 * 显示Fragment
 */
fun FragmentActivity.showFragment(f: Fragment) {
    supportFragmentManager.inTransaction { show(f) }
}


/**
 * 隐藏软键盘
 */
fun Activity.hideSoftInput() {
    (currentFocus ?: window.decorView).hideSoftInput()
}

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
fun Context.getScreenRotation(): Int =
        when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
