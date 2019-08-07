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

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

/**
 * @description TextView的扩展函数
 *
 * @author dugang.
 * @email timor.du@hotmail.com
 * @date  2019/8/7 17:15
 */

/**
 * 给TextView添加内容改变监听
 */
fun TextView.addTextChangedListener(func: (TextWatcher.() -> Unit)) = addTextChangedListener(TextWatcher().apply(func))


class TextWatcher : TextWatcher {
    //
    private var _afterTextChanged: ((s: Editable?) -> Unit)? = null

    fun afterTextChanged(func: ((s: Editable?) -> Unit)) {
        _afterTextChanged = func
    }

    override fun afterTextChanged(s: Editable?) {
        _afterTextChanged?.invoke(s)
    }

    //
    private var _beforeTextChanged: ((s: CharSequence?, start: Int, count: Int, after: Int) -> Unit)? = null

    fun beforeTextChanged(func: ((s: CharSequence?, start: Int, count: Int, after: Int) -> Unit)) {
        _beforeTextChanged = func
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        _beforeTextChanged?.invoke(s, start, count, after)
    }

    //
    private var _onTextChanged: ((s: CharSequence?, start: Int, before: Int, count: Int) -> Unit)? = null

    fun onTextChanged(func: ((s: CharSequence?, start: Int, before: Int, count: Int) -> Unit)) {
        _onTextChanged = func
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        _onTextChanged?.invoke(s, start, before, count)
    }
}