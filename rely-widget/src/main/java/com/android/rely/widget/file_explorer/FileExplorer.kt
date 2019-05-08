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

package com.android.rely.widget.file_explorer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import com.android.rely.base.BaseActivity
import com.android.rely.common.*
import com.android.rely.common.listview.BaseAdapter
import com.android.rely.ext.loadImage
import com.android.rely.widget.R
import kotlinx.android.synthetic.main.act_file_explorer.*
import kotlinx.android.synthetic.main.item_file_explorer.view.*
import java.io.File

/**
 * Created by dugang on 2019/1/15.
 */
class FileExplorer : BaseActivity() {
    companion object {
        private const val KEY_RESULT = "files"
        private const val KEY_SINGLE = "key_single"

        fun open(activity: Activity, single: Boolean = true) {
            activity.skipToActivityForResult<FileExplorer>(2000, Bundle().apply {
                putBoolean(KEY_SINGLE, single)
            })
        }

        @Suppress("UNCHECKED_CAST")
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): ArrayList<File>? {
            if (resultCode == Activity.RESULT_OK && requestCode == 2000) {
                data?.let {
                    return it.getSerializableExtra(KEY_RESULT) as ArrayList<File>
                }
            }
            return null
        }
    }

    private val viewModel: FileExplorerViewModel by lazy { getViewModel<FileExplorerViewModel>() }
    private lateinit var fileExplorerAdapter: FileExplorerAdapter
    private val isSingle: Boolean by lazy { intent.getBooleanExtra(KEY_SINGLE, true) }

    override val layoutResId: Int = R.layout.act_file_explorer

    override fun initView() {
        initToolBar("文件选择", R.mipmap.icon_nav_back)
        //搜索
        et_search.addTextChangedListener {
            afterTextChanged {
                viewModel.search(it.toString())
            }
        }
        //回根路径
        root_path.setOnClickListener {
            viewModel.skipDir(-1)
            path_container.removeAllViews()
        }
        //文件及文件夹列表
        fileExplorerAdapter = FileExplorerAdapter(mContext)
        listView.adapter = fileExplorerAdapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val file = fileExplorerAdapter.getItem(position)
            if (file.isDirectory) {
                addPathView(file)
            } else {
                if (isSingle) {
                    closeActivity(Bundle().apply {
                        putSerializable(KEY_RESULT, arrayListOf(file))
                    })
                }
            }
        }
        //初始化数据
        viewModel.fileFilter()
    }

    override fun initObserve() {
        viewModel.fileList.observe(this, Observer {
            fileExplorerAdapter.updateData(it)
        })
        viewModel.checkedFiles.observe(this, Observer {
            invalidateOptionsMenu()
        })
    }

    private fun addPathView(file: File) {
        val pathName = viewModel.dirSelected(file)
        val textView = TextView(mContext).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                leftMargin = mContext.getDimensionPixelOffset(R.dimen.dp_5)
            }
            setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getCompatDrawable(R.mipmap.arrow_right), null)
            compoundDrawablePadding = mContext.getDimensionPixelOffset(R.dimen.dp_5)

            setTextColor(mContext.getCompatColor(R.color.color_9))
            text = pathName
            tag = path_container.childCount
            setOnClickListener {
                viewModel.skipDir(tag as Int)
                for (i in path_container.childCount - 1 downTo (tag as Int) + 1) {
                    path_container.removeViewAt(i)
                }
            }
        }
        path_container.addView(textView)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return if (viewModel.dirBack()) {
                path_container.removeViewAt(path_container.childCount - 1)
                true
            } else {
                super.onKeyDown(keyCode, event)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!isSingle) {
            menuInflater.inflate(R.menu.menu_file_explorer, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (!isSingle) {
            val size = viewModel.checkedFiles.value?.size ?: 0
            menu?.getItem(0)?.title = "确定($size)"
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.file_explorer_confirm) {
            closeActivity(Bundle().apply {
                putSerializable(KEY_RESULT, viewModel.checkedFiles.value ?: arrayListOf<File>())
            })
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * FileExplorerAdapter
     */
    inner class FileExplorerAdapter(context: Context) : BaseAdapter<File>(context) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: mInflater.inflate(R.layout.item_file_explorer, parent, false)
            if (view.tag == null) view.tag = ItemViewHolder(view)
            (view.tag as ItemViewHolder).initData(position)
            return view
        }

        @SuppressLint("SetTextI18n")
        inner class ItemViewHolder(view: View) : ViewHolder(view) {

            override fun initData(position: Int) {
                val file = getItem(position)

                itemView.item_image.loadImage(getFileType(file.absolutePath))
                itemView.item_name.text = file.name
                itemView.item_arrow.visibility = if (file.isFile) View.GONE else View.VISIBLE

                itemView.item_check.visibility = if (file.isFile && !isSingle) View.VISIBLE else View.GONE
                itemView.item_check.isChecked = viewModel.checkedFiles.value?.contains(file) ?: false
                itemView.item_check.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (!buttonView.isPressed) return@setOnCheckedChangeListener
                    if (isChecked) viewModel.addFile(file) else viewModel.removeFile(file)
                }

                if (file.isFile)
                    itemView.item_desc.text = file.length().formatFileLength()
                else
                    itemView.item_desc.text = "${file.listFiles { _, name -> return@listFiles !name.startsWith(".") }.size}项"

                itemView.item_time.text = file.lastModified().format2Str("yyyy/MM/dd HH:mm")
            }


            private fun getFileType(path: String): Int {
                if (path.isFolderPath()) return R.mipmap.icon_file
                when (path.substring(path.lastIndexOf(".") + 1, path.length)) {
                    "doc" -> R.mipmap.icon_file_doc
                    "docx" -> R.mipmap.icon_file_docx
                    "xls" -> R.mipmap.icon_file_xls
                    "xlsx" -> R.mipmap.icon_file_xlsx
                    "ppt" -> R.mipmap.icon_file_ppt
                    "pptx" -> R.mipmap.icon_file_pptx
                    "rar" -> R.mipmap.icon_file_rar
                    "zip" -> R.mipmap.icon_file_zip
                }
                return R.mipmap.icon_file_unknow
            }
        }
    }
}