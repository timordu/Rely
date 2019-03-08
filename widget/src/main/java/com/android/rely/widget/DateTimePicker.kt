/*
 *      Copyright (c) 2017-2019 dugang
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 */

package com.android.rely.widget

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by dugang on 2016/03/28.日期,时间选择器
 */
//todo 改造成使用wheelView来展示
@Suppress("DEPRECATION", "unused")
class DateTimePicker(private val mContext: Context) {

    /**
     * 日期选择器
     *
     * @param pattern 日期格式化字符串
     * @param onPickListener 日期选择监听器
     */
    fun showDatePickDialog(dateStr: String? = null, pattern: String = "yyyy-MM-dd", onPickListener: (dateStr: String) -> Unit) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            val calendar = Calendar.getInstance()
            dateStr?.let { calendar.time = sdf.parse(it) }

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val onDateSetListener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
                calendar.set(y, m, d)
                onPickListener.invoke(sdf.format(calendar.time))
            }
            DatePickerDialog(mContext, AlertDialog.THEME_HOLO_LIGHT, onDateSetListener, year, month, day).show()
        } catch (e: ParseException) {
            Toast.makeText(mContext, "Date format error", Toast.LENGTH_SHORT).show()
        }

    }


    /**
     * 时间选择器
     *
     * @param pattern    时间格式化字符串
     * @param onPickListener 时间选择监听器
     */
    fun showTimePickerDialog(pattern: String = "HH:mm", onPickListener: (timeStr: String) -> Unit) {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val onTimeSetListener = TimePickerDialog.OnTimeSetListener { _, h, m ->
            calendar.set(Calendar.HOUR_OF_DAY, h)
            calendar.set(Calendar.MINUTE, m)

            onPickListener.invoke(sdf.format(calendar.time))
        }
        TimePickerDialog(mContext, AlertDialog.THEME_HOLO_LIGHT, onTimeSetListener, hour, minute, true).show()
    }
}
