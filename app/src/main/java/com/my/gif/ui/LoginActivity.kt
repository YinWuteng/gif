package com.my.gif.ui

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.transition.Fade
import android.support.transition.TransitionManager
import android.transition.Transition
import android.view.View
import com.my.core.GifFun
import com.my.core.GlobalUtil
import com.my.core.extension.logWarn
import com.my.core.extension.showToast
import com.my.core.model.Version
import com.my.gif.MainActivity
import com.my.gif.R
import com.my.gif.common.SimpleTransitionListener
import com.my.gif.event.FinishActivityEvent
import com.my.gif.event.MessageEvent
import com.my.gif.util.AndroidVersion
import com.my.gif.util.ResponseHandler
import com.my.network.Response
import com.my.network.model.FetchVCode
import com.my.network.model.PhoneLogin
import com.my.network.request.Callback
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.regex.Pattern

/**
 * author:ywt
 * time:2019/6/17  21:10
 * desc:登录
 */
class LoginActivity : AuthActivity() {

    /**
     * 倒计时控件
     */
    private lateinit var timer: CountDownTimer

    private var isLogin = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun setupViews() {
        super.setupViews()
        val isStartWithTransition = intent.getBooleanExtra(START_WITH_TRANSITION, false);

        //api>21且开启动画时
        if (AndroidVersion.hasLollipop() && isStartWithTransition) {
            isTransitioning = true
            window.sharedElementEnterTransition.addListener(object : SimpleTransitionListener() {
                override fun onTransitionEnd(transition: Transition?) {
                    val event = FinishActivityEvent()
                    event.activityClass = LoginActivity::class.java
                    EventBus.getDefault().post(event)
                    isTransitioning = false
                    fadeElementsIn()
                }
            })
        } else {
            loginLayoutBottom.visibility = View.VISIBLE
            loginBgWallLayout.visibility = View.VISIBLE
        }
        timer = SMSTimer(60 * 1000, 1000)
        getVerifyCode.setOnClickListener {

            val number = phoneNumberEdit.text.toString()
            if (number.isEmpty()) {
                showToast(GlobalUtil.getString(R.string.phone_number_is_empty))
                return@setOnClickListener
            }
            val pattern = "^1\\d{10}\$"
            if (!Pattern.matches(pattern, number)) {
                showToast(GlobalUtil.getString(R.string.phone_number_is_invalid))
                return@setOnClickListener
            }
            getVerifyCode.isClickable = false

            FetchVCode.getResponse(number, object : Callback {
                override fun onResonse(response: Response) {
                    if (response.status == 0) {
                        timer.start()
                        verifyCodeEdit.requestFocus()
                    } else {
                        showToast(response.msg)
                        getVerifyCode.isCursorVisible = true
                    }
                }

                override fun onFailed(e: Exception) {
                    logWarn(TAG, e.message, e)
                    ResponseHandler.handleFailure(e)
                    getVerifyCode.isClickable = true
                }
            })
        }

        loginButton.setOnClickListener {
            if (isLogin) return@setOnClickListener
            val number = phoneNumberEdit.text.toString()
            val code = verifyCodeEdit.text.toString()
            if (number.isEmpty() || code.isEmpty()) {
                showToast(GlobalUtil.getString(R.string.phone_number_or_code_is_empty))
                return@setOnClickListener
            }
            val pattern = "^1\\d{10}\$"
            if (!Pattern.matches(pattern, number)) {
                showToast(GlobalUtil.getString(R.string.phone_number_is_invalid))
                return@setOnClickListener
            }
            processLogin(number, code)
        }
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

    private fun processLogin(number: String, code: String) {
        hideSoftKeyboard()
        loginInProcess(true)
        PhoneLogin.getResponse(number, code, object : Callback {
            override fun onResonse(response: Response) {
                if (!ResponseHandler.handleResponse(response)) {
                    val thirdPartyLogin = response as PhoneLogin
                    val status = thirdPartyLogin.status
                    val msg = thirdPartyLogin.msg
                    val userId = thirdPartyLogin.userId
                    val token = thirdPartyLogin.token
                    when (status) {
                        0 -> {
                            hideSoftKeyboard()
                            //处理登录成功时的逻辑，包括数据缓存，界面跳转
                            saveAuthData(userId, token, TYPE_PHONE_LOGIN)
                            getUserBaseInfo()
                        }
                        10101 -> {
                            hideSoftKeyboard()
                            //跳转注册界面
                            RegisterActivity.registerByPhone(this@LoginActivity, number, code)
                            loginInProcess(false)
                        }
                        else -> {
                            logWarn(TAG, GlobalUtil.getResponseClue(status, msg))
                            showToast(response.msg)
                            loginInProcess(false)
                        }
                    }

                } else {
                    loginInProcess(false)
                }
            }

            override fun onFailed(e: Exception) {

                logWarn(TAG, e.message, e)
                ResponseHandler.handleFailure(e)
                loginInProcess(false)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    /**
     * 根据用户是否正在注册来刷新界面。如果正在处理就显示进度条，否则的话就显示输入框
     * @param isProcess 是否在注册
     */
    private fun loginInProcess(isProcess: Boolean) {
        if (AndroidVersion.hasMarshmallow() && !(isProcess && loginRootLayout.keyboardShowed)) {
            TransitionManager.beginDelayedTransition(loginRootLayout, Fade())
        }
        isLogin = isProcess
        if (isProcess) {
            loginInputElements.visibility = View.INVISIBLE
            loginProgressBar.visibility = View.VISIBLE
        } else {
            loginProgressBar.visibility = View.INVISIBLE
            loginInputElements.visibility = View.VISIBLE
        }
    }

    /**
     * 将LoginActivity的界面元素使用淡入的方式显示出来
     */
    private fun fadeElementsIn() {
        TransitionManager.beginDelayedTransition(loginLayoutBottom, Fade())
        loginLayoutBottom.visibility = View.VISIBLE
        TransitionManager.beginDelayedTransition(loginBgWallLayout, Fade())
        loginBgWallLayout.visibility = View.VISIBLE
    }

    inner class SMSTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        /**
         * 结束倒计时
         */
        override fun onFinish() {
            getVerifyCode.text = GlobalUtil.getString(R.string.fetch_vcode)
            getVerifyCode.isClickable = true
        }

        /**
         * 倒计时中
         */
        override fun onTick(millisUntilFinished: Long) {
            getVerifyCode.text = String.format(
                GlobalUtil.getString(R.string.sms_is_sent)
                , millisUntilFinished / 1000
            )
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
        fun actionStartWithTransition(activity: Activity, logo: View, hasNewVersion: Boolean, version: Version?) {
            val intent = Intent(ACTION_LOGIN_WITH_TRANSITION).apply {
                putExtra(INTENT_HAS_NEW_VERSION, hasNewVersion)
                putExtra(INTENT_VERSION, version)
            }
            if (AndroidVersion.hasLollipop()) {
                intent.putExtra(START_WITH_TRANSITION, true)
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    activity, logo, activity.getString(R.string.transition_logo_splash)
                )
                activity.startActivity(intent, options.toBundle())
            } else {
                activity.startActivity(intent)
                activity.finish()
            }
        }
    }
}