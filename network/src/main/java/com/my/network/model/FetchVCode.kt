package com.my.network.model

import com.my.network.Response
import com.my.network.request.Callback
import com.my.network.request.FetchVCodeRequest

/**
 * author:ywt
 * time:2019/6/26  21:07
 * desc:获取短信验证码请求的实体类封装
 */
class FetchVCode : Response() {
    companion object {
        fun getResponse(number: String, callback: Callback) {

            FetchVCodeRequest().number(number).listen(callback)
        }
    }
}