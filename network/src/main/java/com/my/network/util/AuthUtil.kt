package com.my.network.util

import android.text.TextUtils
import com.my.core.Const
import com.my.core.SharedUtil
import java.lang.StringBuilder

/**
 * author:ywt
 * time:2019/6/19  21:02
 * desc:服务器身份证相关的工具类
 */
object AuthUtil {
    /**
     * 判断用户是否已登录
     * @return 已登录返回true，未登录返回false
     */
    val isLogin: Boolean
        get() {
            val u = SharedUtil.read(Const.Auth.USER_ID, 0L)
            val t = SharedUtil.read(Const.Auth.TOKEN, "")
            val lt = SharedUtil.read(Const.Auth.LOGIN_TYPE, -1)
            return u > 0 && !TextUtils.isEmpty(t) && lt >= 0
        }

    /**
     * 当前登录用户的Id
     */
    val userId: Long
        get() = SharedUtil.read(Const.Auth.USER_ID, 0L)

    /**
     * 获取当前登录用户
     */
    val token: String
        get() = SharedUtil.read(Const.Auth.TOKEN, "")

    /**
     * 获取服务器校验码，使用和服务器端相同的算法生产服务器校验码，对接口的安全性进行保护，防止服务器进行恶意攻击
     * @param params 参与生成服务器校验码的参数
     * @return 服务器校验码
     */
    fun getServerVerifyCode(vararg  params:String):String{

        if (params.isNotEmpty()){
            val builder=StringBuilder()
            var needSeparator=false
            for (param in params){
                if (needSeparator){
                    builder.append(",")
                }
                builder.append(param)
                needSeparator=true
            }
            return MD5.encrypt(builder.toString())
        }
        return ""
    }
}