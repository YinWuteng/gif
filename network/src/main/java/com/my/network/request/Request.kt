package com.my.network.request

import com.google.gson.GsonBuilder
import com.my.core.GifFun
import com.my.core.extension.logVerbose
import com.my.network.Response
import com.my.network.exception.ResponseCodeException
import com.my.network.model.OriginThreadCallback
import com.my.network.util.AuthUtil
import com.my.network.util.NetworkConst
import com.my.network.util.Utility
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.OkHttpClient
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * author:ywt
 * time:2019/6/17  21:38
 * desc:网络请求模式的基类，所有请求封装都应该要继承此类。提供网络模块的配置，以及请求的具体逻辑处理
 */
abstract class Request {
    private lateinit var okHttpClient: OkHttpClient

    private val okhttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder().addNetworkInterceptor(LoggingInterceptor())

    private var callback: Callback? = null

    private var params: Map<String, String>? = null

    var getParamsAlready = false

    var deviceName: String

    var deviceSerial: String

    init {
        connectTimeout(10)
        writeTimeout(10)
        readTimeout(10)
        deviceName = Utility.devideName
        deviceSerial = Utility.getDeviceSerial()

    }

    fun build() {
        okHttpClient = okhttpBuilder.build()

    }

    fun connectTimeout(seconds: Int) {
        okhttpBuilder.connectTimeout(seconds.toLong(), TimeUnit.SECONDS)
    }

    fun writeTimeout(seconds: Int) {
        okhttpBuilder.writeTimeout(seconds.toLong(), TimeUnit.SECONDS)
    }

    fun readTimeout(seconds: Int) {
        okhttpBuilder.readTimeout(seconds.toLong(), TimeUnit.SECONDS)
    }

    /**
     * 设置响应回调接口
     * @param callback 回调的接口实例
     */
    fun setListener(callback: Callback) {
        this.callback = callback
    }

    /**
     * 组装网络请求后添加到HTTP发送队列，并监听响应回调
     */
    fun <T : Response> inFlight(requestModel: Class<T>) {
        build()
        val requestBuild = okhttp3.Request.Builder()
        if (method() == GET && getParams() != null) {
            requestBuild.url(urlWithParams())
        } else {
            requestBuild.url(url())
        }
        requestBuild.headers(headers(Headers.Builder()).build())
        when {
            method() == POST -> requestBuild.post(formBody())
            method() == PUT -> requestBuild.put(formBody())
            method() == DELETE -> requestBuild.delete(formBody())
        }
        okHttpClient.newCall(requestBuild.build()).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                notifyFailure(e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: okhttp3.Response) {

                try {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val result = if (body != null) {
                            body.string()
                        } else {
                            ""
                        }
                        logVerbose(LoggingInterceptor.TAG, result)
                        //转化为相应的实体bean
                        val gson = GsonBuilder().disableHtmlEscaping().create()
                        val responseModel = gson.fromJson(result, requestModel)
                        response.close()
                        notifyResponse(responseModel)
                    } else {
                        notifyFailure(ResponseCodeException(response.code()))
                    }
                } catch (e: Exception) {
                    notifyFailure(e)
                }
            }

        })
    }

    abstract fun method(): Int
    abstract fun url(): String
    abstract fun listen(callback: Callback)

    /**
     * 构建和服务器身份认证相关的请求参数
     * @param params
     * 构建参数的params对象
     * @return 如果完成了身份认证参数构建，返回true，否则返回false
     */
    fun buildAuthParams(params: MutableMap<String, String>?): Boolean {
        if (params != null && AuthUtil.isLogin) {
            val userId = AuthUtil.userId.toString()
            val token = AuthUtil.token
            params[NetworkConst.UID] = userId
            params[NetworkConst.DEVICE_SERIAL] = deviceSerial
            params[NetworkConst.TOKEN] = token
            return true
        }
        return false
    }

    /**
     * 根据传入的key值构建用于服务器验证的参数，并添加到请求头当中
     * @param builder
     * 请求头builder
     * @param keys
     * 用于进行服务器验证的键
     */
    fun buildAuthHeaders(builder: Headers.Builder?, vararg keys: String) {
        if (builder != null && keys.isNotEmpty()) {
            val params = mutableListOf<String>()
            for (i in keys.indices) {
                val key = keys[i]
                getParams()?.let {
                    val p = it[key]
                    if (p != null) {
                        params.add(p)
                    }
                }
            }
            builder.add(NetworkConst.VERIFY, AuthUtil.getServerVerifyCode(*params.toTypedArray()))
        }
    }

    /**
     * 获取本次请求所携带的所有参数
     */
    private fun getParams(): Map<String, String>? {
        if (!getParamsAlready) {
            params = params()
            getParamsAlready = true
        }
        return params
    }

    open fun params(): Map<String, String>? {
        return null
    }

    /**
     * 当GET请求携带参数的时候，将参数以key=value的形式拼接到GET请求URL
     * 后面，并且中间以?符号隔开
     * @return 携带参数额URL请求地址
     */
    private fun urlWithParams(): String {
        val params = getParams()
        if (params != null) {
            val keys = params.keys
            if (!keys.isEmpty()) {
                val paramsBuild = StringBuffer()
                var needAnd = false
                for (key in keys) {
                    if (needAnd) {
                        paramsBuild.append("&")
                    }
                    paramsBuild.append(key).append("=").append(params[key])
                    needAnd = true
                }
                return url() + "?" + paramsBuild.toString()
            }
        }
        return url()
    }

    /**
     * Android客户端的所有请求都需要添加User-Agent: GifFun Android这样一个请求
     * 每个接口的封装子类可以添加自己的请求头
     * @param builder
     * 请求头builder
     * @return 添加完请求头后的builder
     */
    open fun headers(builder: Headers.Builder): Headers.Builder {
        builder.add(NetworkConst.HEADER_USER_AGENT, NetworkConst.HEADER_USER_AGENT_VALUE)
        builder.add(NetworkConst.HEADER_APP_VERSION, Utility.appVserion)
        builder.add(NetworkConst.HEADER_APP_SIGN, Utility.appSign)
        return builder
    }

    /**
     * 构建POST,PUT,DELETE请求的参数体
     */
    private fun formBody(): FormBody {
        val builder = FormBody.Builder()
        val params = getParams()
        if (params != null) {
            val kes = params.keys
            if (!kes.isEmpty()) {
                for (key in kes) {
                    val value = params[key]
                    if (value != null) {
                        builder.add(key, value)
                    }
                }
            }
        }
        return builder.build()
    }

    /**
     * 当请求响应成功的时候，将服务器响应转换后的实体进行回调
     */
    private fun notifyResponse(response: Response) {
        callback?.let {
            if (it is OriginThreadCallback) {
                it.onResonse(response)
                callback = null
            } else {
                GifFun.getHandler().post {
                    it.onResonse(response)
                    callback = null
                }
            }
        }
    }

    /**
     * 当请求响应失败的时候，将具体的异常进行回调
     */
    private fun notifyFailure(e: Exception) {

        callback?.let {
            if (it is OriginThreadCallback) {
                it.onFailed(e)
                callback = null
            } else {
                GifFun.getHandler().post {
                    it.onFailed(e)
                    callback = null
                }
            }
        }
    }

    companion object {
        const val GET = 0
        const val POST = 1
        const val PUT = 2
        const val DELETE = 3
    }
}