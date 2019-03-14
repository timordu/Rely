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

package com.android.rely.common

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.view.View
import android.webkit.WebView
import android.widget.GridView
import android.widget.ListView
import android.widget.ScrollView
import com.blankj.ALog
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


/**
 * ByteArray转bitmap
 */
fun ByteArray.toBitmap(): Bitmap = BitmapFactory.decodeByteArray(this, 0, size)

/**
 * Bitmap转ByteArray
 */
fun Bitmap.toByteArray(cf: Bitmap.CompressFormat): ByteArray =
        ByteArrayOutputStream().apply {
            compress(cf, 100, this)
        }.toByteArray()

/**
 * Drawable转Bitmap
 */
fun Drawable.toBitmap(): Bitmap {
    val config = if (opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
    draw(canvas)
    return bitmap
}

/**
 * 保存View到本地. 默认路径：/DCIM/Screenshots/Screenshot_yyyy_MM_dd_HH_mm_ss.png
 */
fun View.saveBitmap(savePath: String = "/DCIM/Screenshots") {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    val canvas = Canvas(bitmap)
    draw(canvas)

    bitmap.saveBitmap(context, savePath)
}

fun ScrollView.saveBitmap(savePath: String = "/DCIM/Screenshots") {
    var h = 0
    for (i in 0 until childCount) {
        h += getChildAt(i).height
    }
    val bitmap = Bitmap.createBitmap(width, h, Bitmap.Config.RGB_565)
    draw(Canvas(bitmap))
    bitmap.saveBitmap(context, savePath)
}

fun ListView.saveBitmap(savePath: String = "/DCIM/Screenshots") {
    var h = 0
    for (i in 0 until childCount) {
        h += getChildAt(i).height
    }
    val bitmap = Bitmap.createBitmap(width, h, Bitmap.Config.RGB_565)
    draw(Canvas(bitmap))
    bitmap.saveBitmap(context, savePath)
}

fun GridView.saveBitmap(savePath: String = "/DCIM/Screenshots") {
    val rows = childCount / numColumns + if (childCount % numColumns == 0) 0 else 1
    var h = 0
    for (i in 0 until rows) {
        h += getChildAt(numColumns * i).height
    }
    val bitmap = Bitmap.createBitmap(width, h, Bitmap.Config.RGB_565)
    draw(Canvas(bitmap))
    bitmap.saveBitmap(context, savePath)
}

@Suppress("DEPRECATION")
fun WebView.saveBitmap(savePath: String = "/DCIM/Screenshots") {
    val snapShot = capturePicture()
    val bitmap = Bitmap.createBitmap(snapShot.width, snapShot.height, Bitmap.Config.RGB_565)
    snapShot.draw(Canvas(bitmap))
    bitmap.saveBitmap(context, savePath)
}

fun Bitmap.saveBitmap(context: Context, savePath: String = "/DCIM/Screenshots") {
    try {
        val rootPath = savePath.createFolder()
        val fileName = "Screenshot_${currentTime.format2Str("yyyy_MM_dd_HH_mm_ss")}.png"
        val saveFile = File("$rootPath/$fileName")

        val outputStream = FileOutputStream(saveFile)
        compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        recycle()

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.DATA, "$rootPath/$fileName")
        }
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        context.showToast("图片保存至: $savePath/$fileName")
    } catch (e: Exception) {
        ALog.e(e.message)
    }
}