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

package com.android.rely.mvvm.widget.image

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.SharedElementCallback
import androidx.viewpager.widget.ViewPager
import com.android.rely.mvvm.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.act_image_preview.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.concurrent.thread

/**
 * Created by dugang on 2018/10/15.
 */
@RuntimePermissions
class ImagePreview : AppCompatActivity() {
    companion object {
        const val KEY_INDEX = "index"
        const val KEY_URLS = "urls"

        /**
         * 显示单张图片
         */
        fun show(activity: Activity, imageView: ImageView, url: String) {
            show(activity, imageView, 0, arrayListOf(url))
        }

        /**
         * 显示一组图片
         */
        fun show(activity: Activity, imageView: ImageView, index: Int, urls: ArrayList<String>) {
            imageView.transitionName = "image"
            val intent = Intent(activity, ImagePreview::class.java).apply {
                putExtra(KEY_INDEX, index)
                putExtra(KEY_URLS, urls)
            }
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView, "image")
            ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle())
        }
    }


    private val viewList = mutableListOf<PhotoView>()
    private lateinit var mAdapter: ImagePreviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_image_preview)

        val list = intent.getStringArrayListExtra(KEY_URLS)
        index_view.visibility = if (list.size > 1) View.VISIBLE else View.GONE

        list.forEach {
            viewList.add(PhotoView(this).apply { setTag(R.id.tag_id, it) })
        }
        mAdapter = ImagePreviewAdapter(viewList)
        viewPager.adapter = mAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                index_view.text = "${position + 1}/${viewList.size}"
            }
        })
        viewPager.setOnReleaseListener(object : DragViewPager.OnReleaseListener {
            override fun onRelease() {
                finishActivity()
            }
        })
        viewPager.currentItem = intent.getIntExtra(KEY_INDEX, 0)


        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                sharedElements?.clear()
                sharedElements?.put("image", mAdapter.getItem(viewPager.currentItem))
            }
        })

        save_image.setOnClickListener {
            writeExternalStorageWithPermissionCheck()
        }
    }

    @Suppress("DEPRECATION")
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun writeExternalStorage() {
        thread {
            val path = viewList[viewPager.currentItem].getTag(R.id.tag_id) as String
            val file = Glide.with(this).load(path).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
            val fis = FileInputStream(file)
            val newFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), path.substring(path.lastIndexOf("/") + 1, path.length)).apply { createNewFile() }
            val fos = FileOutputStream(newFile)
            val buffer = ByteArray(1024)
            do {
                val length = fis.read(buffer)
                if (length != -1) {
                    fos.write(buffer, 0, length)
                } else {
                    break
                }
            } while (true)
            fos.close()
            fis.close()
            runOnUiThread {
                Toast.makeText(this, "保存成功,位置:${newFile.absoluteFile}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun finishActivity() {
        val intent = Intent().apply {
            putExtra(KEY_INDEX, viewPager.currentItem)
        }
        setResult(Activity.RESULT_OK, intent)
        ActivityCompat.finishAfterTransition(this)
    }

    override fun onBackPressed() {
        finishActivity()
    }
}