package com.my.network.request

import com.my.core.GifFun
import com.my.network.model.PhoneRegister
import com.my.network.util.NetworkConst

/**
 * author:ywt
 * time:2019/7/6  15:54
 * desc:注册使用手机号登录账号请求。对应服务器接口：/register/phone
 */
class PhoneRegisterRequest : Request() {
    private var number: String = ""

    private var code: String = ""

    private var nickname: String = ""

    fun number(number: String): PhoneRegisterRequest {
        this.number = number
        return this
    }

    fun code(code: String): PhoneRegisterRequest {
        this.code = code
        return this
    }

    fun nickname(nickname: String): PhoneRegisterRequest {
        this.nickname = nickname
        return this
    }

    override fun url(): String {
        return URL
    }

    override fun method(): Int {
        return POST
    }

    override fun listen(callback: Callback?) {
        setListener(callback)
        inFlight(PhoneRegister::class.java)
    }

    override fun params(): Map<String, String>? {
        val params = HashMap<String, String>()
        params[NetworkConst.NUMBER] = number
        params[NetworkConst.CODE] = code
        params[NetworkConst.NICKNAME] = nickname
        params[NetworkConst.DEVICE_NAME] = deviceName
        params[NetworkConst.DEVICE_SERIAL] = deviceSerial
        return params
    }

    companion object {

        private val URL = GifFun.BASE_URL + "/register/phone"
    }
}