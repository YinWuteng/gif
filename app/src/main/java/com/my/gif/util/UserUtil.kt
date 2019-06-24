package com.my.gif.util

import com.my.core.Const
import com.my.core.SharedUtil

/**
 * author:ywt
 * time:2019/6/24  21:16
 * desc:获取当前登录用户信息的工具类
 */
object UserUtil {
    val nickname:String
    get() = SharedUtil.read(Const.User.NICKNAME,"")

    val avatar:String
    get() = SharedUtil.read(Const.User.AVATAR,"")

    val bgImage:String
    get() = SharedUtil.read(Const.User.BG_IMAGE,"")

    val description:String
    get() = SharedUtil.read(Const.User.DESCRIPTION,"")

    fun savaNickName(nickname:String?){
        if (nickname!=null && nickname.isNotBlank()){
            SharedUtil.save(Const.User.NICKNAME,nickname)
        }else{
            SharedUtil.clear(Const.User.NICKNAME)
        }
    }

    fun saveAvatar(avatar: String?) {
        if (avatar != null && avatar.isNotBlank()) {
            SharedUtil.save(Const.User.AVATAR, avatar)
        } else {
            SharedUtil.clear(Const.User.AVATAR)
        }
    }

    fun saveBgImage(bgImage: String?) {
        if (bgImage != null && bgImage.isNotBlank()) {
            SharedUtil.save(Const.User.BG_IMAGE, bgImage)
        } else {
            SharedUtil.clear(Const.User.BG_IMAGE)
        }
    }

    fun saveDescription(description: String?) {
        if (description != null && description.isNotBlank()) {
            SharedUtil.save(Const.User.DESCRIPTION, description)
        } else {
            SharedUtil.clear(Const.User.DESCRIPTION)
        }
    }
}