package com.my.gif

import android.app.Application
import com.my.core.GifFun
import org.litepal.LitePal

/**
 * author:ywt
 * time:2019/6/30  15:01
 * desc: Application ,在这里进行全局的初始化
 */
open class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GifFun.initialize(this)
        LitePal.initialize(this)
    }
}