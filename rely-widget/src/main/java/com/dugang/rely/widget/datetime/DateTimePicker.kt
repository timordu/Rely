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

package com.dugang.rely.widget.datetime

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.LinearLayout
import android.widget.NumberPicker
import com.dugang.rely.common.extension.format2Str
import com.dugang.rely.common.extension.getCompatColor
import com.dugang.rely.common.extension.getDimensionPixelSize
import com.dugang.rely.common.extension.parseStr2Date
import com.android.rely.widget.R
import kotlinx.android.synthetic.main.dlg_datetime.*
import java.util.*


/**
 * Created by dugang on 2016/03/28.日期,时间选择器
 */
@Suppress("unused")
class DateTimePicker(context: Context, private val type: Int, value: String?, pattern: String, private val onPickListener: (dateStr: String) -> Unit) : Dialog(context),
        NumberPicker.Formatter, NumberPicker.OnValueChangeListener {

    companion object {
        fun selectDate(context: Context, value: String? = null, pattern: String = "yyyy-MM-dd", onPickListener: (dateStr: String) -> Unit) {
            DateTimePicker(context, 1, value, pattern, onPickListener).show()
        }

        fun selectTime(context: Context, value: String? = null, pattern: String = "HH:mm", onPickListener: (dateStr: String) -> Unit) {
            DateTimePicker(context, 2, value, pattern, onPickListener).show()
        }

        fun selectDateTime(context: Context, value: String? = null, pattern: String = "yyyy-MM-dd HH:mm", onPickListener: (dateStr: String) -> Unit) {
            DateTimePicker(context, 0, value, pattern, onPickListener).show()
        }
    }


    init {
        setContentView(R.layout.dlg_datetime)

        when (type) {
            0 -> datetime_layout.visibility = View.VISIBLE
            1 -> time_layout.visibility = View.GONE
            2 -> date_layout.visibility = View.GONE
        }

        val currentCalendar = Calendar.getInstance(Locale.getDefault()).apply {
            value?.let {
                time = it.parseStr2Date(pattern)
            }
        }
        year.setValue(currentCalendar.get(Calendar.YEAR), 1950, 2050)
        month.setValue(currentCalendar.get(Calendar.MONTH) + 1, 1, 12)
        day.setValue(currentCalendar.get(Calendar.DAY_OF_MONTH), 1, getMaxDays(year.value, month.value))
        hour.setValue(currentCalendar.get(Calendar.HOUR_OF_DAY), 0, 23)
        minute.setValue(currentCalendar.get(Calendar.MINUTE), 0, 59)
        showDateTime()

        cancel.setOnClickListener {
            cancel()
        }
        confirm.setOnClickListener {
            val calendar = Calendar.getInstance(Locale.getDefault()).apply {
                set(Calendar.YEAR, year.value)
                set(Calendar.MONTH, month.value - 1)
                set(Calendar.DAY_OF_MONTH, day.value)
                set(Calendar.HOUR_OF_DAY, hour.value)
                set(Calendar.MINUTE, minute.value)
            }
            onPickListener.invoke(calendar.timeInMillis.format2Str(pattern))
            dismiss()
        }
    }

    // 根据月份修改每月可选的最大天数
    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        if (picker?.id == R.id.month)
            day.setValue(day.value, 1, getMaxDays(year.value, month.value))

        showDateTime()
    }

    @SuppressLint("SetTextI18n")
    private fun showDateTime() {
        val array = arrayOf("日", "一", "二", "三", "四", "五", "六")

        val yearValue = year.value
        val monthValue = month.value
        val dayValue = day.value
        val hourValue = hour.value
        val minuteValue = minute.value

        val calendar = Calendar.getInstance(Locale.getDefault()).apply {
            set(Calendar.YEAR, yearValue)
            set(Calendar.MONTH, monthValue - 1)
            set(Calendar.DAY_OF_MONTH, dayValue)
        }
        val week = array[calendar.get(Calendar.DAY_OF_WEEK) - 1]

        datetime.text = StringBuffer()
                .append("${format(yearValue)}年")
                .append("${format(monthValue)}月")
                .append("${format(dayValue)}日")
                .append("星期$week")
                .append("${format(hourValue)}时")
                .append("${format(minuteValue)}分")
                .toString()
    }

    //计算指定年月的最大天数
    private fun getMaxDays(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance(Locale.getDefault()).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DATE, 1)
            roll(Calendar.DATE, -1)
        }
        return calendar.get(Calendar.DATE)
    }

    // 格式化显示数字
    override fun format(value: Int): String {
        return if (value < 10) "0$value" else value.toString()
    }

    private fun NumberPicker.setValue(displayValue: Int, minValue: Int, maxValue: Int) {
        setMaxValue(maxValue)
        setMinValue(minValue)
        value = displayValue

        setOnValueChangedListener(this@DateTimePicker)

        //格式化显示值
        setFormatter(this@DateTimePicker)
        //设置不可编辑
        descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        (layoutParams as LinearLayout.LayoutParams).apply {
            if (type == 0)
                marginStart = if (id != R.id.hour) 0 else context.getDimensionPixelSize(R.dimen.dp_10)
            marginEnd = 0
        }


        //修改分割线的颜色和高度
        NumberPicker::class.java.getDeclaredField("mSelectionDivider").apply {
            isAccessible = true
            set(this@setValue, ColorDrawable(context.getCompatColor(R.color.colorAccent)))
        }
        NumberPicker::class.java.getDeclaredField("mSelectionDividerHeight").apply {
            isAccessible = true
            set(this@setValue, context.getDimensionPixelSize(R.dimen.dp_0_5))
        }
    }
}
