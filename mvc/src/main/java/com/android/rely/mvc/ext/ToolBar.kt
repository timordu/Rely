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

package com.android.rely.mvc.ext

import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import com.android.rely.mvc.base.BaseFragment
import com.android.rely.mvc.R
import com.android.rely.mvc.base.BaseActivity

/**
 * Created by dugang on 2018/12/27.
 */
fun BaseActivity.initToolBar(@StringRes title: Int, @DrawableRes backRes: Int = 0) {
    initToolBar(getString(title), backRes)
}

fun BaseActivity.initToolBar(title: String, @DrawableRes backRes: Int = 0) {
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

fun BaseFragment.initToolBar(view: View, @StringRes title: Int, @DrawableRes backRes: Int = 0, @MenuRes menu: Int = 0) {
    initToolBar(view, getString(title), backRes, menu)
}

fun BaseFragment.initToolBar(view: View, title: String, @DrawableRes backRes: Int = 0, @MenuRes menu: Int = 0) {
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
    if (menu != 0) toolbar.inflateMenu(menu)
    toolbar.setOnMenuItemClickListener(this)
}