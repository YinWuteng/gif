package com.my.network.request

import com.my.core.GifFun
import com.my.network.model.GetBaseInfo
import com.my.network.util.NetworkConst
import okhttp3.Headers
import java.net.URL

/**
 * author:ywt
 * time:2019/6/17  21:37
 * desc:获取当前用户的基本信息请求，对应服务器接口:/user/baseinfo
 */
class GetBaseInfoRequest : Request() {


    override fun method(): Int {
        return GET
    }

    override fun url(): String {
        return URL
    }

    override fun listen(callback: Callback?) {
        setListener(callback)
        inFlight(GetBaseInfo::class.java)
    }

    override fun params(): Map<String, String>? {
        val params = HashMap<String, String>()
        return if (buildAuthParams(params)) {
            params
        } else super.params()
    }

    override fun headers(builder: Headers.Builder): Headers.Builder {
        buildAuthHeaders(builder, NetworkConst.DEVICE_SERIAL, NetworkConst.TOKEN)
        return super.headers(builder)
    }

    companion object {
        private val URL = GifFun.BASE_URL + "/user/baseinfo"
    }
}