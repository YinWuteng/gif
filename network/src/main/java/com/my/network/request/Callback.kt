package com.my.network.request

import com.my.network.Response

/**
 * author:ywt
 * time:2019/6/17  21:34
 * desc:网络请求回调接口
 */
interface Callback {

    fun onResponse(response: Response)
    
    fun onFailure(e: Exception)
}