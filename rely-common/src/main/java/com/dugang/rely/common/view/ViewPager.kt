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

import androidx.viewpager.widget.ViewPager

/**
 * @description ViewPager的扩展函数
 *
 * @author dugang.
 * @email timor.du@hotmail.com
 * @date  2019/8/7 17:15
 */

fun ViewPager.addOnPageChangeListener(func: (OnPageChangeListener.() -> Unit)) = addOnPageChangeListener(OnPageChangeListener().apply(func))

class OnPageChangeListener : ViewPager.OnPageChangeListener {
    private var _onPageScrollStateChanged: ((state: Int) -> Unit)? = null

    private var _onPageScrolled: ((position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit)? = null

    private var _onPageSelected: ((position: Int) -> Unit)? = null


    fun onPageScrollStateChanged(func: ((state: Int) -> Unit)) {
        _onPageScrollStateChanged = func
    }

    fun onPageScrolled(func: ((position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit)) {
        _onPageScrolled = func
    }

    fun onPageSelected(func: ((position: Int) -> Unit)) {
        _onPageSelected = func
    }

    override fun onPageScrollStateChanged(state: Int) {
        _onPageScrollStateChanged?.invoke(state)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        _onPageScrolled?.invoke(position, positionOffset, positionOffsetPixels)
    }

    override fun onPageSelected(position: Int) {
        _onPageSelected?.invoke(position)
    }
}