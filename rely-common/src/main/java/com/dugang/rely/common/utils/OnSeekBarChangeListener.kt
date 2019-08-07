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

package com.dugang.rely.common.utils

import android.widget.SeekBar

class OnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    private var _onProgressChanged: ((seekBar: SeekBar?, progress: Int, fromUser: Boolean) -> Unit)? = null

    private var _onStartTrackingTouch: ((seekBar: SeekBar?) -> Unit)? = null

    private var _onStopTrackingTouch: ((seekBar: SeekBar?) -> Unit)? = null

    fun onProgressChanged(onProgressChanged: (seekBar: SeekBar?, progress: Int, fromUser: Boolean) -> Unit) {
        _onProgressChanged = onProgressChanged
    }

    fun onStartTrackingTouch(onStartTrackingTouch: (seekBar: SeekBar?) -> Unit) {
        _onStartTrackingTouch = onStartTrackingTouch
    }

    fun onStopTrackingTouch(onStopTrackingTouch: (seekBar: SeekBar?) -> Unit) {
        _onStopTrackingTouch = onStopTrackingTouch
    }


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        _onProgressChanged?.invoke(seekBar, progress, fromUser)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        _onStartTrackingTouch?.invoke(seekBar)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        _onStopTrackingTouch?.invoke(seekBar)
    }
}