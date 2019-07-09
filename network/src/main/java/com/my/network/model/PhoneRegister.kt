package com.my.network.model

import com.my.network.request.Callback
import com.my.network.request.PhoneRegisterRequest

/**
 * author:ywt
 * time:2019/7/6  15:48
 * desc:注册手机号登录账号的实体类封装
 */
class PhoneRegister : BaseRegister() {
    companion object {
        fun getResponse(number: String, code: String, nickname: String, callback: Callback) {
            PhoneRegisterRequest()
                .number(number)
                .code(code)
                .nickname(nickname)
                .listen(callback)
        }
    }
}