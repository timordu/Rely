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

package com.android.rely.widget.fingerprint

import android.content.Context
import androidx.core.hardware.fingerprint.FingerprintManagerCompat

/**
 * Created by dugang on 2018/10/26. 指纹验证工具类
 */
@Suppress("unused")
object FingerprintUtil {
    /**
     * 判断硬件是否支持指纹识别
     */
    @JvmStatic
    fun isHardwareSupport(context: Context): Boolean =
            FingerprintManagerCompat.from(context).isHardwareDetected

    /**
     * 判断是否已经注册有指纹
     */
    @JvmStatic
    fun hasEnrolledFingerprints(context: Context): Boolean =
            FingerprintManagerCompat.from(context).hasEnrolledFingerprints()

    /**
     * 显示指纹验证弹窗
     */
    fun showAuthDialog(context: Context, onAuthListener: (success: Boolean, message: String?) -> Unit) {
        if (!isHardwareSupport(context))
            onAuthListener.invoke(false, "当前设备不支持指纹识别")
        else if (!hasEnrolledFingerprints(context))
            onAuthListener.invoke(false, "当前设备暂无指纹信息")
        else {
            FingerprintAuthDialog(context, onAuthListener).show()
        }
    }
}