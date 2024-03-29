package com.my.network.exception

/**
 * author:ywt
 * time:2019/6/19  20:28
 * desc:当服务器响应的头不在200与300之间时，说明请求出现了异常，此时应该将此异常主动抛出
 */
class ResponseCodeException(val responseCode:Int):RuntimeException("Http request failed with response code $responseCode")