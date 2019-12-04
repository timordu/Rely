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

package com.dugang.rely.extension

import android.content.Context
import android.widget.ImageView
import com.dugang.rely.common.extension.PUBLIC_DOWNLOAD_DIR
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.concurrent.thread


fun ImageView.loadImage(path: Any, options: RequestOptions = RequestOptions()) {
    Glide.with(context).load(path).apply(options).into(this)
}

fun ImageView.loadCircleImage(path: Any, options: RequestOptions = RequestOptions()) {
    Glide.with(context).load(path).apply(RequestOptions.circleCropTransform()).apply(options).into(this)

}

fun ImageView.loadRoundCornerImage(path: Any, radius: Int = 20, options: RequestOptions = RequestOptions()) {
    Glide.with(context).load(path).apply(RequestOptions.bitmapTransform(RoundedCorners(radius))).apply(options)
        .into(this)
}

fun ImageView.loadClear() {
    Glide.with(context).clear(this)
}

fun Context.downloadImage(
    path: String,
    saveDir: String = PUBLIC_DOWNLOAD_DIR,
    resultListener: (path: String?) -> Unit
) {
    thread {
        try {
            val file = Glide.with(this).load(path).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
            val fis = FileInputStream(file)
            val newFile =
                File(saveDir, path.substring(path.lastIndexOf("/") + 1, path.length)).apply { createNewFile() }
            val fos = FileOutputStream(newFile)
            val buffer = ByteArray(1024)
            do {
                val length = fis.read(buffer)
                if (length != -1) fos.write(buffer, 0, length) else break
            } while (true)
            fos.close()
            fis.close()
            resultListener.invoke(newFile.absolutePath)
        } catch (e: Exception) {
            resultListener.invoke(null)
        }
    }
}