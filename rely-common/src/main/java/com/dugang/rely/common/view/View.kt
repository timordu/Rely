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

package com.dugang.rely.common.view

import android.view.View
import android.view.inputmethod.InputMethodManager
import com.dugang.rely.common.extension.inputMethodManager
/**
 * @description View的扩展函数
 *
 * @author dugang.
 * @email timor.du@hotmail.com
 * @date  2019/8/7 17:15
 */


fun View.showSoftInput() {
    requestFocus()
    context.inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun View.hideSoftInput() {
    clearFocus()
    context.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun View.isSoftInputActive(): Boolean = context.inputMethodManager.isActive









