package com.my.network.request

import com.my.core.GifFun
import com.my.network.model.PhoneLogin
import com.my.network.util.NetworkConst

/**
 * author:ywt
 * time:2019/6/26  21:35
 * desc:使用手机号登录请求。
 */
class PhoneLoginRequest : Request() {

    private var number: String = ""
    private var code: String = ""

    fun number(number: String): PhoneLoginRequest {

        this.number = number
        return this
    }

    fun code(code: String): PhoneLoginRequest {
        this.code = code
        return this
    }


    override fun method(): Int {

        return POST
    }

    override fun url(): String {
        return URL
    }

    override fun listen(callback: Callback?) {
        setListener(callback)
        inFlight(PhoneLogin::class.java)
    }

    override fun params(): Map<String, String>? {
        val params = HashMap<String, String>()
        params[NetworkConst.NUMBER] = number
        params[NetworkConst.CODE] = code
        params[NetworkConst.DEVICE_NAME] = deviceName
        params[NetworkConst.DEVICE_SERIAL] = deviceSerial
        return params
    }

    companion object {

        private val URL = GifFun.BASE_URL + "/login/phone"
    }
}