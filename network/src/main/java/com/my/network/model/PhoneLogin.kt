package com.my.network.model

import com.google.gson.annotations.SerializedName
import com.my.network.Response
import com.my.network.request.Callback
import com.my.network.request.PhoneLoginRequest

/**
 * author:ywt
 * time:2019/6/26  21:32
 * desc:手机号登录的实体类封装
 */
class PhoneLogin : Response() {

    /**
     * 用户的注册账号id
     */
    @SerializedName("user_id")
    var userId: Long = 0

    /**
     * 记录用户的登录身份，token有效期30天
     */
    var token = ""

    companion object {
        fun getResponse(number: String, code: String, callback: Callback) {

            PhoneLoginRequest()
                .number(number)
                .code(code)
                .listen(callback)
        }
    }
}