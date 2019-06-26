package com.my.gif.ui

import android.app.Activity
import android.content.Intent

/**
 * author:ywt
 * time:2019/6/26  21:50
 * desc:注册Activity
 */
class RegisterActivity : AuthActivity() {
    override fun forwardToMainactivity() {

    }

    companion object {
        private const val TAG = "RegisterActivity"
        const val INTENT_OPEN_ID = "intent_open_id"

        const val INTENT_ACCESS_TOKEN = "intent_access_token"

        const val INTENT_NICKNAME = "intent_nickname"

        const val INTENT_PHONE_NUMBER = "intent_phone_number"

        const val INTENT_VERIFY_CODE = "intent_verify_code"

        const val INTENT_LOGIN_TYPE = "intent_login_type"

        /**
         * 检查用户昵称是否合法的正式表达式。
         */
        const val NICK_NAME_REG_EXP = "^[\u4E00-\u9FA5A-Za-z0-9_\\-]+$"

        fun registerByPhone(activity: Activity, number: String, code: String) {
            val intent = Intent(activity, RegisterActivity::class.java)
            intent.putExtra(INTENT_PHONE_NUMBER, number)
            intent.putExtra(INTENT_VERIFY_CODE,code)
            intent.putExtra(INTENT_LOGIN_TYPE, TYPE_PHONE_LOGIN)
            activity.startActivity(intent)
        }
    }
}