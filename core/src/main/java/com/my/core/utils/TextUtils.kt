package com.my.core.utils

import java.util.regex.Pattern

/**
 * author:ywt
 * time:2019/7/4  21:12
 * desc:文字校验类工具
 */
object TextUtils {

    /**
     * 检查用户昵称是否合法的正式表达式。
     */
    const val NICK_NAME_REG_EXP = "^[\u4E00-\u9FA5A-Za-z0-9_\\-]+$"
    /**
     * 是不是手机号码
     */
    fun isPhone(number: String): Boolean {
        val pattern = "^1\\d{10}\$"
        return Pattern.matches(pattern, number)
    }

}