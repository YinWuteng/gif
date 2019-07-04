package com.my.core.model

import java.util.regex.Pattern

/**
 * author:ywt
 * time:2019/7/4  21:12
 * desc:文字校验类工具
 */
object TextUtils {

    /**
     * 是不是手机号码
     */
    fun isPhone(number: String): Boolean {
        val pattern = "^1\\d{10}\$"
        return Pattern.matches(pattern, number)
    }
}