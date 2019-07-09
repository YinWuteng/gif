package com.my.gif.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.transition.Fade
import android.support.transition.TransitionManager
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.my.core.GlobalUtil
import com.my.core.extension.logDebug
import com.my.core.extension.logWarn
import com.my.core.extension.showToast
import com.my.gif.R
import com.my.gif.event.FinishActivityEvent
import com.my.gif.util.AndroidVersion
import com.my.gif.util.ResponseHandler
import com.my.network.Response
import com.my.network.model.BaseRegister
import com.my.network.model.PhoneRegister
import com.my.network.request.Callback
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus

/**
 * author:ywt
 * time:2019/6/26  21:50
 * desc:注册Activity
 */
class RegisterActivity : AuthActivity(), TextView.OnEditorActionListener {

    /**
     * 登录类型（QQ,微信、微博或手机号）
     */
    private var loginType = 0

    /**
     * 是否正在注册中
     */
    private var isRegistering = false

    lateinit var nicknameEditText: EditText

    lateinit var nicknameLayout: TextInputLayout

    private var number = ""
    private var code = ""

    /**
     * 判断用户昵称是否合法。用户昵称长度必须在2-30个字符之间，并且只能包含中英文、数字、下划线和横线。
     *
     * @return 昵称合法返回true，不合法返回false。
     */
    val isNocinameVaild: Boolean
        get() {
            val nickname = nicknameEditText.text.toString()
            if (nickname.length < 2) {
                nicknameInputLayout.isErrorEnabled = true
                nicknameInputLayout.error = GlobalUtil.getString(R.string.nickname_length_invalid)
                return false
            } else if (!nickname.matches(NICK_NAME_REG_EXP.toRegex())) {
                nicknameInputLayout.isErrorEnabled = true
                nicknameInputLayout.error = GlobalUtil.getString(R.string.nickname_invalid)
                return false
            }
            return true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    /**
     * 获取从Intent中传递过来的数据并显示到界面上
     */
    override fun setupViews() {
        super.setupViews()
        setupToolbar()
        nicknameEditText = nicknameEdit
        nicknameLayout = nicknameInputLayout
        title = "" //注册界面的Toolbar不需要title

        moveOnText.setOnClickListener {
            doRegister()
        }

        nicknameEditText.setOnEditorActionListener(this)
        loginType = intent.getIntExtra(INTENT_LOGIN_TYPE, 0)

        if (intent.getStringExtra(INTENT_PHONE_NUMBER) == null || intent.getStringExtra(INTENT_VERIFY_CODE) == null) {
            showToast(GlobalUtil.getString(R.string.phone_number_verify_code_is_null))
            finish()
            return
        }

        number = intent.getStringExtra(INTENT_PHONE_NUMBER)
        code = intent.getStringExtra(INTENT_VERIFY_CODE)
        nicknameEditText.requestFocus()

    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {

        if (actionId == EditorInfo.IME_ACTION_GO) {
            doRegister()
        }
        return false
    }

    /**
     * 根据用户是否正在注册来刷新界面，如果正在处理就显示进度条，否则就显示输入框
     */
    private fun registerInProgress(isProgress: Boolean) {
        if (AndroidVersion.hasMarshmallow()) {
            TransitionManager.beginDelayedTransition(registerRootLayout, Fade())
        }
        isRegistering = isProgress
        if (isProgress) {
            moveOnText.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            nicknameInputLayout.visibility = View.GONE
        } else {
            moveOnText.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            nicknameInputLayout.visibility = View.VISIBLE
        }
    }

    /**
     * 注册
     */
    private fun doRegister() {

        if (isRegistering) return
        when (loginType) {
            TYPE_PHONE_LOGIN -> processPhoneRegister()
        }
    }

    /**
     * 注册手机号登录账号
     */
    fun processPhoneRegister() {
        if (isNocinameVaild) {
            hideSoftKeyboard()
            nicknameLayout.isErrorEnabled = false
            registerInProgress(true)
            sendPhoneRegisterRequest()
        }
    }

    fun sendPhoneRegisterRequest() {

        PhoneRegister.getResponse(number, code, nicknameEditText.text.toString().trim(), object : Callback {
            override fun onResponse(response: Response) {
                handleRegisterCallback(response)
            }

            override fun onFailure(e: Exception) {
                logWarn(TAG, e.message, e)
                registerInProgress(false)
                ResponseHandler.handleFailure(e)
            }

        })
    }

    private fun handleRegisterCallback(response: Response) {
        if (activity == null) return
        if (!ResponseHandler.handleResponse(response)) {
            val register = response as BaseRegister
            val status = register.status
            when (status) {
                0 -> {
                    logDebug(TAG, "token is " + register.token + " , getAvatar is " + register.avatar)
                    val userId = register.userId
                    val token = register.token
                    saveAuthData(userId.toLong(), token, loginType)
                    // registerSuccess()
                    //处理成功时的逻辑，包括数据缓存，界面跳转
                    getUserBaseInfo()
                }
                10105 -> {

                    registerInProgress(false)
                    nicknameLayout.isErrorEnabled = true
                    nicknameLayout.error = GlobalUtil.getString(R.string.register_failed_nickname_is_used)
                }
                else -> {
                    logWarn(TAG, "Register failed. " + GlobalUtil.getResponseClue(status, register.msg))
                    showToast(register.msg)
                    finish()
                }
            }
        } else {
            finish()
        }
    }

    override fun forwardToMainactivity() {
        MainActivity.actionStart(this)
        val event = FinishActivityEvent()
        //通知登录Activity销毁
        event.activityClass = LoginActivity::class.java
        EventBus.getDefault().post(event)
        finish()
    }

    companion object {
        private const val TAG = "RegisterActivity"
        const val INTENT_OPEN_ID = "intent_open_id"

        const val INTENT_ACCESS_TOKEN = "intent_access_token"

        const val INTENT_NICKNAME = "intent_nickname"

        const val INTENT_PHONE_NUMBER = "intent_phone_number"

        const val INTENT_VERIFY_CODE = "intent_verify_code"

        const val INTENT_LOGIN_TYPE = "intent_login_type"

        /**
         * 检查用户昵称是否合法的正式表达式。
         */
        const val NICK_NAME_REG_EXP = "^[\u4E00-\u9FA5A-Za-z0-9_\\-]+$"

        fun registerByPhone(activity: Activity, number: String, code: String) {
            val intent = Intent(activity, RegisterActivity::class.java)
            intent.putExtra(INTENT_PHONE_NUMBER, number)
            intent.putExtra(INTENT_VERIFY_CODE, code)
            intent.putExtra(INTENT_LOGIN_TYPE, TYPE_PHONE_LOGIN)
            activity.startActivity(intent)
        }
    }
}