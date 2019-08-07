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

package com.dugang.rely.condition_skip

import android.content.Context
import androidx.annotation.IntRange

/**
 * 验证条件,优先级数值越高优先级越高
 */
abstract class Valid(val context: Context, @IntRange(from = 1, to = 10) val priority: Int = 5) : Comparable<Valid> {

    override fun compareTo(other: Valid): Int = when {
        this.priority > other.priority -> -1
        this.priority < other.priority -> 1
        else -> 0
    }

    abstract fun check(): Boolean

    abstract fun doValid()
}
