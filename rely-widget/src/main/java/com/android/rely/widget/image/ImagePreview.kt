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

package com.android.rely.widget.image

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.AbsListView
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.SharedElementCallback
import androidx.viewpager.widget.ViewPager
import com.android.rely.base.BaseActivity
import com.android.rely.common.EXTERNAL_DIR_ROOT
import com.android.rely.common.PUBLIC_DOWNLOAD_DIR
import com.android.rely.common.showToast
import com.android.rely.widget.R
import com.blankj.ALog
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
class ImagePreview : BaseActivity() {
    companion object {
        private const val KEY_INDEX = "index"
        private const val KEY_URLS = "urls"

        fun show(activity: AppCompatActivity, imageView: ImageView, urls: ArrayList<String>, index: Int = 0) {
            ALog.d("index:$index")
            imageView.transitionName = "share_image"
            val intent = Intent(activity, ImagePreview::class.java).apply {
                putExtra(KEY_INDEX, index)
                putExtra(KEY_URLS, urls)
            }
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView, "image")
            activity.startActivity(intent, optionsCompat.toBundle())
        }

        fun onActivityReenter(activity: AppCompatActivity, data: Intent?, absListView: AbsListView, @IdRes imageView: Int) {
            data?.extras?.let {
                val currentPosition = it.getInt(KEY_INDEX, -1)
                if (currentPosition == -1) return
                absListView.smoothScrollToPosition(currentPosition)
                activity.setExitSharedElementCallback(object : SharedElementCallback() {
                    override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                        names?.clear()
                        sharedElements?.clear()
                        names?.add("share_image")
                        sharedElements?.put("share_image", absListView.getChildAt(currentPosition).findViewById(imageView))

                    }
                })
            }
        }
    }

    private val viewList = mutableListOf<PhotoView>()
    private lateinit var mAdapter: ImagePreviewAdapter

    override val layoutResId: Int = R.layout.act_image_preview

    override fun initView() {
        postponeEnterTransition()
        val list = intent.getStringArrayListExtra(KEY_URLS)
        index_view.visibility = if (list.size > 1) View.VISIBLE else View.GONE

        list.indices.forEach {
            viewList.add(PhotoView(this).apply {
                setTag(R.id.tag_id, list[it])
                transitionName = "share_image"
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
                ALog.d("$position,${viewPager.currentItem}")
            }
        })
        viewPager.setOnReleaseListener { onBackPressed() }
        viewPager.currentItem = intent.getIntExtra(KEY_INDEX, 0)

        save_image.setOnClickListener {
            saveImageWithPermissionCheck()
        }
        startPostponedEnterTransition()
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

    override fun finishAfterTransition() {
        val intent = Intent().apply {
            val enterPos = intent.getIntExtra(KEY_INDEX, 0)
            val exitPos = viewPager.currentItem
            putExtra(KEY_INDEX, if (enterPos == exitPos) -1 else exitPos)
        }
        setResult(Activity.RESULT_OK, intent)
        super.finishAfterTransition()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val enterPos = intent.getIntExtra(KEY_INDEX, 0)
        val exitPos = viewPager.currentItem
        if (enterPos != exitPos) {
            setEnterSharedElementCallback(object : SharedElementCallback() {
                override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                    names?.clear()
                    sharedElements?.clear()
                    names?.add("share_image")
                    sharedElements?.put("share_image", mAdapter.getItem(viewPager.currentItem))
                }
            })
        }
    }
}