package com.my.gif.ui

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.my.core.GifFun
import com.my.core.model.Version
import com.my.gif.MainActivity
import com.my.gif.R
import com.my.gif.event.FinishActivityEvent
import com.my.gif.event.MessageEvent
import com.my.gif.util.AndroidVersion
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * author:ywt
 * time:2019/6/17  21:10
 * desc:登录
 */
class LoginActivity : AuthActivity() {

    /**
     * 倒计时控件
     */
    private lateinit var timer:CountDownTimer

    private var  isLogin=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    /**
     * 是否在进行transition动画
     */
    private var isTransitioning = false

    override fun onBackPressed() {
        if (!isTransitioning) {
            finish()
        }
    }

    override fun forwardToMainactivity() {
        //登录成功，跳转到应用主界面
        MainActivity.actionStart(this)
        finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onMessageEvent(messageEvent: MessageEvent) {
        if (messageEvent is FinishActivityEvent && LoginActivity::class.java == messageEvent.activityClass) {
            finish()
        }
    }

    companion object {
        private const val TAG = "LoginActivity"

        @JvmStatic
        val START_WITH_TRANSITION = "start_with_transition"

        @JvmStatic
        val INTENT_HAS_NEW_VERSION = "intent_has_new_version"

        @JvmStatic
        val INTENT_VERSION = "intent_version"

        private val ACTION_LOGIN = "${GifFun.getPackageName()}.ACTION_LOGIN"

        private val ACTION_LOGIN_WITH_TRANSITION = "${GifFun.getPackageName()}.ACTION_LOGIN_WITH_TRANSITION"

        /**
         * 启动LoginActivity
         * @param activity 原activity的实例
         * @param hasNewVersion 是否存在版本更新
         */
        fun actionStart(activity: Activity, hasNewVersion: Boolean, version: Version?) {
            val intent = Intent(ACTION_LOGIN).apply {
                putExtra(INTENT_HAS_NEW_VERSION, hasNewVersion)
                putExtra(INTENT_VERSION, version)
            }
            activity.startActivity(intent)
        }

        /**
         * 启动LoginActivity，并附带transition动画
         * @param activity 原activity实例
         * @param logo 要执行transition动画的控件
         */
        fun actionStartWithTransition(activity: Activity,logo:View,hasNewVersion: Boolean,version: Version?){
            val intent=Intent(ACTION_LOGIN_WITH_TRANSITION).apply {
                putExtra(INTENT_HAS_NEW_VERSION,hasNewVersion)
                putExtra(INTENT_VERSION,version)
            }
            if (AndroidVersion.hasLollipop()){
                intent.putExtra(START_WITH_TRANSITION,true)
                val options=ActivityOptions.makeSceneTransitionAnimation(
                    activity,logo,activity.getString(R.string.transition_logo_splash))
                activity.startActivity(intent,options.toBundle())
            }else{
                activity.startActivity(intent)
                activity.finish()
            }
        }
    }
}