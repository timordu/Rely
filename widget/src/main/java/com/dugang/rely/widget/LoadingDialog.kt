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

package com.dugang.rely.widget

import android.app.Dialog
import android.content.Context

/**
 * Created by dugang on 2017/4/17. 加载Dialog
 */
class LoadingDialog(context: Context, cancellable: Boolean = false) : Dialog(context, R.style.Dialog_Loading) {

    init {
        setContentView(R.layout.dlg_loading)
        setCancelable(cancellable)
        setCanceledOnTouchOutside(false)
    }
}
