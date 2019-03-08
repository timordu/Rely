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
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * Created by dugang on 2018/10/23.
 */

/**
 * 跳转到指定的Activity
 */
fun Fragment.skipToActivity(clazz: Class<out Activity>, bundle: Bundle? = null) {
    val intent = Intent(activity, clazz).apply {
        putExtras(Bundle().apply { bundle?.let { putAll(it) } })
    }
    startActivity(intent)
}

/**
 * 带返回的跳转到指定的Activity
 */
fun Fragment.skipToActivityForResult(clazz: Class<out Activity>, requestCode: Int, bundle: Bundle? = null) {
    val intent = Intent(activity, clazz).apply {
        putExtras(Bundle().apply { bundle?.let { putAll(it) } })
    }
    startActivityForResult(intent, requestCode)
}

/**
 * 跳转到指定的Activity并结束当前Activity
 */
fun Fragment.skipToActivityAndFinish(clazz: Class<out Activity>, bundle: Bundle? = null) {
    skipToActivity(clazz, bundle)
    activity?.finish()
}

/**
 * 从新线程启动指定的Activity
 */
fun Fragment.skipToActivityWithNewTask(clazz: Class<out Activity>, bundle: Bundle? = null) {
    val intent = Intent(activity, clazz).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtras(Bundle().apply { bundle?.let { putAll(it) } })
    }
    startActivity(intent)
    activity?.finish()
}

/**
 * 关闭界面,如果bundle不为空则返回
 */
fun Fragment.closeActivity(bundle: Bundle? = null) {
    activity?.closeActivity(bundle)
}

fun Fragment.replaceFragment(@IdRes layoutId: Int, f: Fragment, bundle: Bundle = Bundle(), tag: String? = null) {
    childFragmentManager.beginTransaction()
            .replace(layoutId, f.apply { arguments = bundle }, tag)
            .commitAllowingStateLoss()
}

fun Fragment.addFragment(@IdRes layoutId: Int, f: Fragment, bundle: Bundle = Bundle(), tag: String? = null) {
    childFragmentManager.beginTransaction()
            .add(layoutId, f.apply { arguments = bundle }, tag)
            .commitAllowingStateLoss()
}

fun Fragment.hideFragment(f: Fragment) {
    childFragmentManager.beginTransaction().hide(f).commitAllowingStateLoss()
}

fun Fragment.showFragment(f: Fragment) {
    childFragmentManager.beginTransaction().show(f).commitAllowingStateLoss()
}

/**
 * Fragment事务扩展
 */
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commitAllowingStateLoss()
}

/**
 * 隐藏软键盘
 */
fun Fragment.hideSoftInput() {
    activity?.hideSoftInput()
}



