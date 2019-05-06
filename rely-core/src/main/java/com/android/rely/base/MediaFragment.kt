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

package com.android.rely.base

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.android.rely.common.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
abstract class MediaFragment : BaseFragment() {
    private var mediaUri: Uri? = null

    /**
     * 选择照片
     */
    fun pickPicture() {
        // todo 自定义图片选择器,实现支持多选
        startActivityForResult(
            Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                .apply { type = "image/*" }, 2001
        )
    }

    open fun pickPictureCallBack(filePath: String) {
        Log.d("media", "pickPictureCallBack: $filePath")

    }

    /**
     * 拍照
     */
    fun takePhoto(dir: String = "/DCIM/Camera") {
        openPhotoCameraWithPermissionCheck(dir)
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun openPhotoCamera(dir: String) {
        val rootPath = "$dir/Camera".createFolder()
        val fileName = "IMG_${currentTime.format2Str("yyyyMMdd_HHmmss")}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            put(MediaStore.Images.Media.DATA, "$rootPath/$fileName")
        }
        mediaUri = mContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, mediaUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }, 2002)
    }

    open fun takePhotoCallBack(filePath: String) {
        Log.d("media", "takePhotoCallBack: $filePath")
    }

    /**
     * 拍摄视频
     */
    fun takeVideo(dir: String = "/DCIM/Camera") {
        openVideoCamera(dir)
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun openVideoCamera(dir: String) {
        val rootPath = "$dir/Camera".createFolder()
        val fileName = "VID_${currentTime.format2Str("yyyyMMdd_HHmmss")}.mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DATA, "$rootPath/$fileName")
        }
        mediaUri = mContext.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        startActivityForResult(Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
            putExtra(MediaStore.EXTRA_OUTPUT, mediaUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }, 2003)
    }

    open fun takeVideoCallBack(filePath: String) {
        Log.d("MediaActivity", "takePictureCallBack: $filePath")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        //选择照片返回
        if (resultCode == Activity.RESULT_OK && requestCode == 2001) {
            intent?.let {
                val filePath = it.data?.let { uri -> getMediaPath(uri) }
                filePath?.let { path -> pickPictureCallBack(path) }
            }
        }

        //拍照返回
        if (requestCode == 2002) {
            try {
                mediaUri?.let { uri ->
                    if (resultCode == Activity.RESULT_OK) {
                        getMediaPath(uri)?.let { takePhotoCallBack(it) }
                    } else {
                        mContext.contentResolver.delete(uri, null, null)
                    }
                }
            } finally {
                mediaUri = null
            }
        }

        //摄像返回
        if (requestCode == 2003) {
            try {
                mediaUri?.let { uri ->
                    if (resultCode == Activity.RESULT_OK) {
                        getMediaPath(uri, true)?.let { takeVideoCallBack(it) }
                    } else {
                        mContext.contentResolver.delete(uri, null, null)
                    }
                }
            } finally {
                mediaUri = null
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun getMediaPath(uri: Uri, isVideo: Boolean = false): String? {
        val projection =
            if (isVideo) arrayOf(MediaStore.Video.VideoColumns.DATA) else arrayOf(MediaStore.Images.ImageColumns.DATA)
        val columnName = if (isVideo) MediaStore.Video.VideoColumns.DATA else MediaStore.Images.ImageColumns.DATA

        if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cursor = mContext.contentResolver.query(uri, projection, null, null, null)
            cursor?.let {
                if (cursor.moveToNext()) {
                    val index = it.getColumnIndex(columnName)
                    if (index >= 0) return it.getString(index)
                }
            }
            cursor?.close()
        } else
            return uri.path
        return null
    }
}