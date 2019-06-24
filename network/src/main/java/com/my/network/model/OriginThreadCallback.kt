package com.my.network.model

import com.my.network.request.Callback

/**
 * author:ywt
 * time:2019/6/19  20:35
 * desc:网络请求响应的回调接口，回调时保留原来线程进行回调，不切换到主线程
 */
interface OriginThreadCallback: Callback