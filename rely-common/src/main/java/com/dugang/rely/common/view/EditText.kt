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

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.method.DigitsKeyListener
import android.text.method.ReplacementTransformationMethod
import android.widget.EditText
import java.util.*

/**
 * @description EditText的扩展函数
 *
 * @author dugang.
 * @email timor.du@hotmail.com
 * @date  2019/8/7 17:15
 */


/**
 * 给EditText添加小数限制器,默认两位小数
 */
fun EditText.addMoneyFilter(digits: Int = 2) {
    val filter = object : DigitsKeyListener(Locale.getDefault()) {
        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
            if (source == "." && TextUtils.isEmpty(dest.toString())) return "0."
            if (source != "." && dest.toString() == "0") return ""

            dest?.let {
                val pos = it.indexOf('.')
                if (pos != -1) {
                    val str = it.substring(pos + 1, it.length)
                    if (str.length >= digits) return ""
                }
            }
            return SpannableStringBuilder(source, start, end)
        }
    }
    filters = arrayOf(filter)
}

/**
 * 给EditText添加内容改变监听
 */
fun EditText.addTextChangedListener(func: (TextWatcher.() -> Unit)) = addTextChangedListener(TextWatcher().apply(func))


/**
 * 给EditText输入大小写字母转换,默认显示大写.注意:输入显示大写,不代表getText()获取的值也为大写
 */
fun EditText.addCapTransformation(needCap: Boolean = true) {
    val lower = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
    val upper = charArrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')

    transformationMethod = object : ReplacementTransformationMethod() {
        override fun getOriginal(): CharArray = if (needCap) lower else upper

        override fun getReplacement(): CharArray = if (needCap) upper else lower
    }
}