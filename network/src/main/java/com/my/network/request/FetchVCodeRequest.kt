package com.my.network.request

import com.my.core.GifFun
import com.my.network.model.FetchVCode
import com.my.network.util.NetworkConst
import okhttp3.Headers

/**
 * author:ywt
 * time:2019/6/26  21:09
 * desc:
 */
class FetchVCodeRequest : Request() {
    private var number = ""
    fun number(number: String): FetchVCodeRequest {
        this.number = number
        return this
    }

    override fun method(): Int {
        return POST
    }

    override fun url(): String {

        return URL
    }

    override fun listen(callback: Callback) {
        setListener(callback)
        inFlight(FetchVCode::class.java)
    }

    override fun params(): Map<String, String>? {
        val params=HashMap<String,String>()
        params[NetworkConst.NUMBER]=number
        params[NetworkConst.DEVICE_NAME]=deviceName
        params[NetworkConst.DEVICE_SERIAL]=deviceSerial
        return params
    }

    override fun headers(builder: Headers.Builder): Headers.Builder {
        buildAuthHeaders(builder,NetworkConst.DEVICE_NAME,NetworkConst.NUMBER,NetworkConst.DEVICE_SERIAL)
        return super.headers(builder)
    }
    companion object {
        private val URL = GifFun.BASE_URL + "/login/fetch_verify_code"
    }
}