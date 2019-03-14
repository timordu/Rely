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

package com.android.rely.common

import java.util.regex.Pattern

//判断对象是空
fun Any?.isNull(): Boolean = this == null

//判断对象非空
fun Any?.isNotNull(): Boolean = this != null

//判断字符串是否符合正则表达式
fun String.isMatches(regex: String): Boolean = Pattern.matches(regex, this)

//判断字符串是Email地址
private const val REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"

fun String.isEmail(): Boolean = isMatches(REGEX_EMAIL)

//判断字符串是身份证号码
private const val REGEX_ID_CARDS = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$"

fun String.isIdCards(): Boolean = isMatches(REGEX_ID_CARDS)

//判断字符是Url地址
private const val REGEX_URL = "[a-zA-z]+://[^\\s]*"

fun String.isUrl(): Boolean = isMatches(REGEX_URL)

//判断字符串是中文
private const val REGEX_ZH = "^[\\u4e00-\\u9fa5]+$"

fun String.isZh(): Boolean = isMatches(REGEX_ZH)

//判断字符串是日期
private const val REGEX_DATE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$"

fun String.isDate(): Boolean = isMatches(REGEX_DATE)

//判断字符串是IP地址
private const val REGEX_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)"

fun String.isIP(): Boolean = isMatches(REGEX_IP)

