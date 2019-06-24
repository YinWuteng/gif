package com.my.gif.common

/**
 * author:ywt
 * time:2019/6/14 20:57
 * desc:在Activity或Fragment中进行网络请求所需要经历的生命周期函数
 */
interface RequestLifecycle {
    
    fun startLoading()

    fun loadFinished()

    fun loadFailed(msg: String?)
}