package com.my.network.request

import com.my.core.extension.logVerbose
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * author:ywt
 * time:2019/6/17  21:44
 * desc:okhttp网络请求日志拦截器，通过日志记录okhttp所有请求以及相应细节
 */
internal class LoggingInterceptor:Interceptor {
   @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

       val  request=chain.request()
       val t1=System.nanoTime()
       logVerbose(TAG,"Sending request:"+request.url()+"/n"+request.headers())

       val  response=chain.proceed(request)
       val t2=System.nanoTime()

       logVerbose(TAG,"Received response for" +response.request().url()+"in"+(t2-t1)/1e6+ "ms\n" + response.headers())
       return response
    }
    companion object{
        const val TAG="LoggingInterceptor"
    }
}