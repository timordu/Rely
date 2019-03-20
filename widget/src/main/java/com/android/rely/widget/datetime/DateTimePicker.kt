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

package com.android.rely.widget.datetime

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.*
import com.android.rely.common.getCompatColor
import com.android.rely.common.getDimensionPixelSize
import com.android.rely.widget.R
import kotlinx.android.synthetic.main.dlg_datetime.*


/**
 * Created by dugang on 2016/03/28.日期,时间选择器
 */
@Suppress("unused", "DEPRECATION")
class DateTimePicker(context: Context) : Dialog(context), NumberPicker.Formatter {
    init {
        setContentView(R.layout.dlg_datetime)

//        datePicker.descendantFocusability = DatePicker.FOCUS_BLOCK_DESCENDANTS
//
//       val lp =  datePicker.layoutParams as LinearLayout.LayoutParams
//        lp.setMargins(0,0,0,0)
//        datePicker.layoutParams = lp
//
//        val yearId = context.getRes("year", "id", "android")
//        val yearNumberPicker = findViewById<NumberPicker>(yearId)
//        setPickerMargin(yearNumberPicker)
//        setDividerColor(yearNumberPicker)
//        setNumberPickerDivider(yearNumberPicker)
//
//
//        val monthId = context.getRes("month", "id", "android")
//        val monthNumberPicker = findViewById<NumberPicker>(monthId)
//        setPickerMargin(monthNumberPicker)
//        setDividerColor(monthNumberPicker)
//        setNumberPickerDivider(monthNumberPicker)
//
//        val dayId = context.getRes("day", "id", "android")
//        val dayNumberPicker = findViewById<NumberPicker>(dayId)
//        setPickerMargin(dayNumberPicker)
//        setDividerColor(dayNumberPicker)
//        setNumberPickerDivider(dayNumberPicker)
//
//        timePicker.setIs24HourView(true)
//        timePicker.descendantFocusability = TimePicker.FOCUS_BLOCK_DESCENDANTS
//
//        val hourId = context.getRes("hour", "id", "android")
//        val hourNumberPicker = findViewById<NumberPicker>(hourId)
//        setPickerMargin(hourNumberPicker)
//        setDividerColor(hourNumberPicker)
//        setNumberPickerDivider(hourNumberPicker)
//
//        val minuteId = context.getRes("minute", "id", "android")
//        val minuteNumberPicker = findViewById<NumberPicker>(minuteId)
//        setPickerMargin(minuteNumberPicker)
//        setDividerColor(minuteNumberPicker)
//        setNumberPickerDivider(minuteNumberPicker)

        year.setValue(2019, 1970, 2030)
        month.setValue(1, 1, 12)
        day.setValue(1, 1, 30)
        hour.setValue(2019, 0, 23)
        minute.setValue(2019, 0, 59)
    }

    override fun format(value: Int): String {
        return if (value < 10) "0$value" else value.toString()
    }

    private fun NumberPicker.setValue(displayValue: Int, minValue: Int, maxValue: Int) {
        setMaxValue(maxValue)
        setMinValue(minValue)
        value = displayValue

        //格式化显示值
        setFormatter(this@DateTimePicker)
        //设置不可编辑
        descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        (layoutParams as LinearLayout.LayoutParams).apply {
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
