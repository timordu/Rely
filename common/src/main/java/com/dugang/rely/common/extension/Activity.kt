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
import com.dugang.rely.common.view.hideSoftInput

/**
 * 跳转到指定的Activity
 */
inline fun <reified T : Activity> Activity.skipToActivity(bundle: Bundle? = null) {
    val intent = Intent(this, T::class.java).apply {
        putExtras(Bundle().apply { bundle?.let { putAll(it) } })
    }
    startActivity(intent)
}

/**
 * 带返回的跳转到指定的Activity
 */
inline fun <reified T : Activity> Activity.skipToActivityForResult(requestCode: Int, bundle: Bundle? = null) {
    val intent = Intent(this, T::class.java).apply {
        putExtras(Bundle().apply { bundle?.let { putAll(it) } })
    }
    startActivityForResult(intent, requestCode)
}

/**
 * 跳转到指定的Activity并结束当前Activity
 */
inline fun <reified T : Activity> Activity.skipToActivityAndFinish(bundle: Bundle? = null) {
    val intent = Intent(this, T::class.java).apply {
        putExtras(Bundle().apply { bundle?.let { putAll(it) } })
    }
    startActivity(intent)
    finish()
}

/**
 * 从新线程启动指定的Activity
 */
inline fun <reified T : Activity> Activity.skipToActivityWithNewTask(bundle: Bundle? = null) {
    val intent = Intent(this, T::class.java).apply {
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


