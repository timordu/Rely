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

package com.dugang.rely.widget.image_viewer

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.dugang.rely.base.BaseActivity
import com.dugang.rely.common.extension.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.dugang.rely.widget.R
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
class ImagePreview : BaseActivity() {
    companion object {
        private const val KEY_INDEX = "index"
        private const val KEY_URLS = "urls"

        fun show(activity: AppCompatActivity, urls: ArrayList<String>, index: Int = 0) {
            activity.skipToActivity<ImagePreview>(Bundle().apply {
                putInt(KEY_INDEX, index)
                putStringArrayList(KEY_URLS, urls)
            })
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private val viewList = mutableListOf<PhotoView>()
    private lateinit var mAdapter: ImagePreviewAdapter

    override val layoutResId: Int = R.layout.act_image_preview

    override fun initView() {
        val list = intent.getStringArrayListExtra(KEY_URLS)
        index_view.visibility = if (list.size > 1) View.VISIBLE else View.GONE

        list.indices.forEach {
            viewList.add(PhotoView(this).apply {
                setTag(R.id.tag_id, list[it])
            })
        }
        mAdapter = ImagePreviewAdapter(viewList)
        viewPager.adapter = mAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                index_view.text = "${position + 1}/${viewList.size}"
            }
        })
        viewPager.setOnReleaseListener { onBackPressed() }
        viewPager.currentItem = intent.getIntExtra(KEY_INDEX, 0)

        save_image.setOnClickListener {
            closeActivity()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun initObserve() {
    }

    @Suppress("DEPRECATION")
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun saveImage() {
        thread {
            val path = viewList[viewPager.currentItem].getTag(R.id.tag_id) as String
            val file = Glide.with(this).load(path).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
            val fis = FileInputStream(file)
            val newFile = File(PUBLIC_DOWNLOAD_DIR, path.substring(path.lastIndexOf("/") + 1, path.length)).apply { createNewFile() }
            val fos = FileOutputStream(newFile)
            val buffer = ByteArray(1024)
            do {
                val length = fis.read(buffer)
                if (length != -1) fos.write(buffer, 0, length) else break
            } while (true)
            fos.close()
            fis.close()
            runOnUiThread {
                showToast("保存成功,位置:${newFile.absolutePath.replace(EXTERNAL_DIR_ROOT, "")}")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}