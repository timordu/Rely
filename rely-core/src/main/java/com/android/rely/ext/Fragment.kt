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
inline fun <reified T : Activity> Fragment.skipToActivity(bundle: Bundle? = null) {
    activity?.skipToActivity<T>(bundle)
}

/**
 * 带返回的跳转到指定的Activity
 */
inline fun <reified T : Activity> Fragment.skipToActivityForResult(requestCode: Int, bundle: Bundle? = null) {
    activity?.skipToActivityForResult<T>(requestCode, bundle)
}

/**
 * 跳转到指定的Activity并结束当前Activity
 */
inline fun <reified T : Activity> Fragment.skipToActivityAndFinish(bundle: Bundle? = null) {
    activity?.skipToActivityAndFinish<T>(bundle)
}

/**
 * 从新线程启动指定的Activity
 */
inline fun <reified T : Activity> Fragment.skipToActivityWithNewTask(bundle: Bundle? = null) {
    activity?.skipToActivityWithNewTask<T>(bundle)
}

/**
 * 关闭界面
 */
fun Fragment.closeActivity(bundle: Bundle? = null) {
    activity?.closeActivity(bundle)
}

fun Fragment.replaceFragment(@IdRes layoutId: Int, f: Fragment, bundle: Bundle = Bundle(), tag: String? = null) {
    childFragmentManager.inTransaction {
        replace(layoutId, f.apply { arguments = bundle }, tag)
    }
}

fun Fragment.addFragment(@IdRes layoutId: Int, f: Fragment, bundle: Bundle = Bundle(), tag: String? = null) {
    childFragmentManager.inTransaction {
        add(layoutId, f.apply { arguments = bundle }, tag)
    }
}

fun Fragment.hideFragment(f: Fragment) {
    childFragmentManager.inTransaction { hide(f) }
}

fun Fragment.showFragment(f: Fragment) {
    childFragmentManager.inTransaction { show(f) }
}


/**
 * 隐藏软键盘
 */
fun Fragment.hideSoftInput() {
    activity?.hideSoftInput()
}



