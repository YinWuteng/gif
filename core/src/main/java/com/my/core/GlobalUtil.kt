package com.my.core

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.my.core.extension.logWarn

/**
 * author:ywt
 * time:2019/6/16  16:10
 * desc:应用程序全局的通用工具类，功能比较单一，经常被复用的功能，应该封装到此工具类当中，从而给全局代码提供方面的操作。
 */
object GlobalUtil {
    private const val TAG = "GlobalUtil"

    /**
     * 获取当前应用程序的包名
     */
    val appPackage: String
        get() {
            return GifFun.getContext().packageName
        }

    /**
     * 将当前线程睡眠指定毫秒数。
     *
     * @param millis
     * 睡眠的时长，单位毫秒。
     */
    fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    /**
     * 获取AndroidManifest.xml <application>标签下的meta-data的值
     */
    fun getApplicationMetaData(key: String): String? {
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo =
                GifFun.getContext().packageManager.getApplicationInfo(appPackage, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            logWarn(TAG, e.message, e)
        }
        if (applicationInfo == null) return ""
        return applicationInfo.metaData.getString(key)

    }

    /**
     * 获取资源文件中定义的字符串
     * @param resId 字符串资源ID
     */
    fun getString(resId: Int): String {
        return GifFun.getContext().resources.getString(resId);
    }

    /**
     * 获取请求结果的线索，即将状态码和简单描述组合成一段调试信息
     * @param status 状态码
     * @param msg 结果描述
     */
    fun getResponseClue(status: Int, msg: String): String {

        return "code: $status,msg: $msg"
    }
}