package com.my.gif.ui

import com.my.core.Const
import com.my.core.GifFun
import com.my.core.GlobalUtil
import com.my.core.SharedUtil
import com.my.core.extension.logWarn
import com.my.core.extension.showToast
import com.my.core.extension.showToastOnUiThread
import com.my.gif.R
import com.my.gif.base.BaseActivity
import com.my.gif.util.ResponseHandler
import com.my.gif.util.UserUtil
import com.my.network.Response
import com.my.network.model.GetBaseInfo
import com.my.network.request.Callback

/**
 * author:ywt
 * time:2019/6/17  21:11
 * desc:登录和注册Activity的基类，用于封装登录和注册时通用的逻辑功能
 */
abstract class AuthActivity : BaseActivity() {

    /**
     * 根据参数中传入的登录类型数值获取登录类型的名称
     * @param loginType 登录类型的名称
     */
    protected fun getLoginTypeName(loginType: Int) = when (loginType) {
        TYPE_QQ_LOGIN -> "QQ"
        TYPE_WECHAT_LOGIN -> "微信"
        TYPE_WEIBO_LOGIN -> "微博"
        TYPE_GUEST_LOGIN -> "游客"
        else -> ""
    }


    /**
     * 存储用户身份的信息
     * @param userId 用户id
     * @param token 用户token
     * @param loginType 登录类型
     */
    protected fun saveAuthData(userId: Long, token: String, loginType: Int) {
        SharedUtil.save(Const.Auth.USER_ID, userId)
        SharedUtil.save(Const.Auth.TOKEN, token)
        SharedUtil.save(Const.Auth.LOGIN_TYPE, loginType)
        GifFun.refreshLoginState()
    }

    /**
     * 获取当前登录用户的基本信息，包括昵称、头像等
     */
    protected fun getUserBaseInfo() {
        GetBaseInfo.getResponse(object : Callback {
            override fun onResonse(response: Response) {
                if (activity == null) return
                if (!ResponseHandler.handleResponse(response)) {//如果结果没有被处理
                    val baseInfo = response as GetBaseInfo
                    val status = baseInfo.status
                    when (status) {
                        0 -> {
                            UserUtil.savaNickName(baseInfo.nickname)
                            UserUtil.saveAvatar(baseInfo.avatar)
                            UserUtil.saveDescription(baseInfo.description)
                            UserUtil.saveBgImage(baseInfo.bgImage)
                            forwardToMainactivity()
                        }
                        10202 -> {
                            showToast(GlobalUtil.getString(R.string.get_baseinfo_failed_user_not_exist))
                            GifFun.logout()
                            finish()
                        }
                        else -> {
                            logWarn(TAG, "Get user baseinfo failed" + GlobalUtil.getResponseClue(status, baseInfo.msg))
                            showToast(GlobalUtil.getString(R.string.get_baseinfo_failed))
                            GifFun.logout()
                            finish()
                        }
                    }
                }else{//结果被通用方法处理了
                    activity?.let {
                       finish()
                    }
                }
            }

            override fun onFailed(e: Exception) {
                logWarn(TAG,e.message,e)
                showToast(GlobalUtil.getString(R.string.get_baseinfo_failed))
                GifFun.logout()
                finish()
            }

        })
    }

    protected abstract fun forwardToMainactivity()

    companion object {
        private const val TAG = "AuthActivity"

        /**
         * QQ第三方登录类型
         */
        const val TYPE_QQ_LOGIN = 1

        /**
         * 微信第三方登录类型
         */
        const val TYPE_WECHAT_LOGIN = 2

        /**
         * 微博第三方登录的类型
         */
        const val TYPE_WEIBO_LOGIN = 3

        /**
         * 手机号登录的类型
         */
        const val TYPE_PHONE_LOGIN = 4

        /**
         * 游客登录的类型，测试环境下有效
         */
        const val TYPE_GUEST_LOGIN = -1
    }
}