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

package com.dugang.rely.common.extension

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat
import java.io.InputStream

/*
  ---------- 获取资源(补充缺少) ----------
 */
fun Context.getCompatColor(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)

fun Context.getCompatDrawable(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(this, id)

fun Context.getStringArray(@ArrayRes id: Int): Array<out String> = resources.getStringArray(id)

fun Context.getIntArray(@ArrayRes id: Int): IntArray = resources.getIntArray(id)

fun Context.getDimension(@DimenRes id: Int): Float = resources.getDimension(id)

fun Context.getDimensionPixelSize(@DimenRes id: Int): Int = resources.getDimensionPixelSize(id)

fun Context.getDimensionPixelOffset(@DimenRes id: Int): Int = resources.getDimensionPixelOffset(id)

fun Context.getRaw(@RawRes id: Int): InputStream = resources.openRawResource(id)

fun Context.getRaw(@RawRes id: Int, value: TypedValue): InputStream = resources.openRawResource(id, value)

fun Context.getRes(resName: String, resType: String = "mipmap", defPackage: String = packageName): Int = resources.getIdentifier(resName, resType, defPackage)

fun Context.getAsset(fileName: String, accessMode: Int = AssetManager.ACCESS_STREAMING): InputStream = assets.open(fileName, accessMode)


