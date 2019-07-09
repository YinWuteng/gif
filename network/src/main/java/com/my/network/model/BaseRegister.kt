package com.my.network.model

import com.google.gson.annotations.SerializedName
import com.my.network.Response

/**
 * author:ywt
 * time:2019/7/6  15:49
 * desc:注册请求所使用的的通用基类
 */
open class BaseRegister :Response(){
    /**
     * 用户的账号id
     */
    @SerializedName("user_id")
    var userId:Int=0

    /**
     * 记录用户的登录身份，token有效期30天
     */
    var token:String=""

    /**
     * 使用三方账号上锁使用的头像。如果账号未注册时会返回此参数
     */
    var avatar:String=""
}