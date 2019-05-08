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

import androidx.lifecycle.MutableLiveData
import com.android.rely.base.BaseViewModel
import com.android.rely.common.EXTERNAL_DIR_ROOT
import java.io.File
import java.util.*
import kotlin.Comparator

/**
 * @author dugang.
 * @email timor.du@hotmail.com
 * @date  2019/5/8 10:54
 */
class FileExplorerViewModel : BaseViewModel() {
    val fileList = MutableLiveData<List<File>>()
    val checkedFiles = MutableLiveData<ArrayList<File>>()

    private var paths = mutableListOf(EXTERNAL_DIR_ROOT)
    private var keyword = ""

    /**
     * 搜索文件或文件夹
     */
    fun search(keyword: String) {
        this.keyword = keyword
        fileFilter()
    }

    /**
     * 根据关键字和路径过滤
     */
    fun fileFilter() {
        val sb = StringBuilder()
        paths.forEach { path -> sb.append("/").append(path) }
        val path = sb.toString()

        val list = File(path).listFiles { _, name ->
            return@listFiles !name.startsWith(".") && name.contains(keyword, true)
        }.asList()

        Collections.sort(list, Comparator<File> { o1, o2 ->
            if (o1.isDirectory && o2.isFile) return@Comparator -1
            if (o1.isFile && o2.isDirectory) return@Comparator 1
            o1.name.compareTo(o2.name)
        })
        fileList.postValue(list)
    }

    /**
     * 目录选择
     */
    fun dirSelected(file: File): String {
        val path = file.absolutePath
        val pathName = path.substring(path.lastIndexOf("/") + 1, path.length)
        paths.add(pathName)
        fileFilter()
        return pathName
    }

    /**
     * 目录回退
     */
    fun dirBack(): Boolean {
        if (paths.size > 1) {
            paths.removeAt(paths.size - 1)
            fileFilter()
            return true
        }
        return false
    }

    fun skipDir(position: Int) {
        paths = paths.subList(0, position + 2)
        fileFilter()
    }

    fun addFile(file: File) {
        val list = checkedFiles.value ?: arrayListOf()
        list.add(file)
        checkedFiles.postValue(list)
    }

    fun removeFile(file: File) {
        val list = checkedFiles.value
        list?.remove(file)
        checkedFiles.postValue(list)
    }
}