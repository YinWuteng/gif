package com.my.network

/**
 * author:ywt
 * time:2019/6/17  21:24
 * desc:请求响应的基类，这里封装了所有请求都必须会响应的参数，status和msg
 */
open class Response {

    /**
     * 请求结果的状态码。所有状态码的含义https://github.com/sharefunworks/giffun-server#2-状态码
     */
    var  status:Int=0

    /**
     * 请求结果的简单描述
     */
    var msg:String=""
}