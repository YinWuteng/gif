package com.my.network.request

import com.my.core.GifFun
import com.my.core.GlobalUtil
import com.my.network.model.Init
import com.my.network.util.NetworkConst
import okhttp3.Headers

/**
 * author:ywt
 * time:2019/6/26  22:02
 * desc:初始化请求
 */
class InitRequest : Request() {
    init {
        connectTimeout(5)
        readTimeout(5)
        writeTimeout(5)
    }

    override fun method(): Int {
        return GET
    }

    override fun url(): String {
        return URL
    }

    override fun listen(callback: Callback) {
        setListener(callback)
        inFlight(Init::class.java)
    }

    override fun params(): Map<String, String>? {
        val params = HashMap<String, String>()
        params[NetworkConst.CLIENT_VERSION] = GlobalUtil.appVersionCode.toString()
        val appChannel = GlobalUtil.getApplicationMetaData("APP_CHANNEL")
        if (appChannel != null) {
            params[NetworkConst.CLIENT_CHANNEL] = appChannel
        }
        if (buildAuthParams(params)) {
            params[NetworkConst.DEVICE_NAME] = deviceName
        }
        return params
    }

    override fun headers(builder: Headers.Builder): Headers.Builder {
        buildAuthHeaders(builder, NetworkConst.UID, NetworkConst.TOKEN)
        return super.headers(builder)
    }

    companion object {

        private val URL = GifFun.BASE_URL + "/init"
    }
}