package com.my.network.util

import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.my.core.GifFun
import com.my.core.GlobalUtil
import com.my.core.SharedUtil
import com.my.core.extension.logWarn
import java.lang.Exception
import java.util.*

/**
 * author:ywt
 * time:2019/6/17  22:03
 * desc:获取各项基础数据的工具类
 */
object Utility {

    const val TAG = "Utility"

    private var deviceSerial: String? = null

    /**
     * 获取设备的品牌和型号
     */
    val deviceName: String
        get() {
            var deviceName = Build.BOARD + " " + Build.MODEL
            if (TextUtils.isEmpty(deviceName)) {
                deviceName = "unknown"
            }
            return deviceName
        }

    /**
     * 获取当前APP的版本号
     */
    val appVersion: String
        get() {
            var version = ""
            try {
                val packageManger = GifFun.getContext().packageManager
                val packInfo = packageManger.getPackageInfo(GifFun.getPackageName(), 0)
                version = packInfo.versionName
            } catch (e: Exception) {
                logWarn(TAG, e.message, e)
            }
            if (TextUtils.isEmpty(version)) {
                version = "unknown"
            }
            return version
        }

    /**
     * 获取APP网络请求验证参数，用于辨识是不是官方渠道的App
     */
    val appSign: String
        get() {
            return MD5.encrypt(SignUtil.getAppSignature() + appVersion)
        }

    /**
     * 获取设备的序列号。如果无法获取到设备的序列号，则会生成一个随机的UUID来作为设备的序列号，UUID生成之后会存入缓存，
     * 下次获取设备序列号的时候会优先从缓存中读取。
     * @return 设备的序列号。
     */
    @SuppressLint("HardwareIds")
    fun getDeviceSerial(): String {
        if (deviceSerial == null) {
            var deviceId: String? = null
            val appChannel = GlobalUtil.getApplicationMetaData("APP_CHANNEL")
            if ("google" != appChannel || "samsung" != appChannel) {
                try {
                    deviceId =
                        Settings.Secure.getString(GifFun.getContext().contentResolver, Settings.Secure.ANDROID_ID)
                } catch (e: Exception) {
                    logWarn(TAG, "get android_id with error", e)
                }
                if (!TextUtils.isEmpty(deviceId) && deviceId!!.length < 255) {
                    deviceSerial = deviceId
                    return deviceSerial.toString()
                }
            }
            var uuid = SharedUtil.read(NetworkConst.UUID, "")
            if (!TextUtils.isEmpty(uuid)) {
                deviceSerial = uuid
                return deviceSerial.toString()
            }
            uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase()
            SharedUtil.save(NetworkConst.UUID, uuid)
            deviceSerial = uuid
            return deviceSerial.toString()
        } else {
            return deviceSerial.toString()
        }
    }

}