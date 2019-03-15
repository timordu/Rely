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

package com.android.rely.eventbus

import android.os.Bundle
import org.greenrobot.eventbus.EventBus


@Suppress("unused")
class MsgEvent(var code: Int) {
    var msg: String? = null
    val data = Bundle()

    constructor(code: Int, msg: String? = null) : this(code) {
        this.msg = msg
    }

    constructor(code: Int, bundle: Bundle? = null) : this(code) {
        bundle?.let {
            this.data.putAll(it)
        }
    }
}

@Suppress("unused")
fun MsgEvent.post() {
    EventBus.getDefault().post(this)
}
