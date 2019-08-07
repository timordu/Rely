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

import android.util.ArrayMap
import java.util.concurrent.PriorityBlockingQueue

class ConditionSkip(private val action: String) {
    companion object {
        private val skipMap = ArrayMap<String, ConditionSkip>()

        fun get(action: String): ConditionSkip? = skipMap[action]

        fun add(action: String): ConditionSkip = ConditionSkip(
            action
        ).apply { skipMap[action] = this }
    }

    private val validQueue = PriorityBlockingQueue<Valid>()
    private var lastValid: Valid? = null
    private var validComplete: (() -> Unit)? = null

    fun addValid(valid: Valid): ConditionSkip {
        validQueue.add(valid)
        return this
    }

    fun validComplete(func: () -> Unit): ConditionSkip {
        this.validComplete = func
        return this
    }

    fun doCall() {
        validQueue.forEach {
            if (it.check()) validQueue.remove(it)
        }
        if (validQueue.size == 0) {
            validComplete?.invoke()
            skipMap.remove(action)
        } else {
            lastValid = validQueue.peek()
            lastValid?.doValid()
        }
    }
}