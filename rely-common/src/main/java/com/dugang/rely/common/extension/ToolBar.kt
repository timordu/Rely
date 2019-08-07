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

import android.annotation.SuppressLint
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.android.rely.common.R

/**
 * Created by dugang on 2018/12/27.
 */
fun AppCompatActivity.initToolBar(@StringRes title: Int, @DrawableRes backRes: Int = 0) {
    initToolBar(getString(title), backRes)
}

fun AppCompatActivity.initToolBar(title: String, @DrawableRes backRes: Int = 0) {
    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    toolbar.title = ""
    setSupportActionBar(toolbar)
    if (backRes != 0) {
        toolbar.setNavigationIcon(backRes)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
    toolbarTitle.text = title
}

@SuppressLint("ResourceType")
fun Fragment.initToolBar(
    view: View, @StringRes title: Int, @DrawableRes backRes: Int = 0, @MenuRes menuId: Int = 0,
    onMenuItemClickListener: ((item: MenuItem) -> Boolean)? = null
) {
    initToolBar(view, getString(title), backRes, menuId, onMenuItemClickListener)
}

fun Fragment.initToolBar(
    view: View,
    title: String, @DrawableRes backRes: Int = 0, @MenuRes menuId: Int = 0,
    onMenuItemClickListener: ((item: MenuItem) -> Boolean)? = null
) {
    val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
    toolbar.title = ""
    if (backRes != 0) {
        toolbar.setNavigationIcon(backRes)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }
    val toolbarTitle = view.findViewById<TextView>(R.id.toolbar_title)
    toolbarTitle.text = title
    if (menuId != 0) {
        toolbar.inflateMenu(menuId)
        toolbar.setOnMenuItemClickListener {
            onMenuItemClickListener?.invoke(it)
            return@setOnMenuItemClickListener false
        }
    }
}