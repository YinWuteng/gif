package com.my.gif.ui

import android.os.Bundle
import android.view.View
import com.my.core.GifFun
import com.my.core.GlobalUtil
import com.my.core.model.Version
import com.my.gif.R
import com.my.gif.base.BaseActivity
import com.my.gif.event.FinishActivityEvent
import com.my.gif.event.MessageEvent
import kotlinx.android.synthetic.main.activity_splash.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * author:ywt
 * time:2019/6/16 14:22
 * desc:闪屏Activity界面，这里进行初始化操作
 */
class SplashActivity : BaseActivity() {
    /**
     * 记录进入SplashActivity的时间
     */
    var  enterTime:Long=0

    /**
     * 判断是否正在跳转或已经跳转到下一个界面
     */
    var isForwarding=false

    var hasNewVersion=false

    lateinit var logoView:View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        logoView=logo
        enterTime=System.currentTimeMillis()
        delayToForward()
    }

    override fun setupViews() {
        startInitRequest()
    }

    override fun onBackPressed() {
        //屏蔽手机的返回键
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onMessageEvent(messageEvent: MessageEvent) {
        if (messageEvent is FinishActivityEvent){
            if (javaClass==messageEvent.activityClass){
                if (!isFinishing){
                    finish()
                }
            }
        }
    }

    /**
     * 开始向服务器发送初始化请求
     */
    private fun startInitRequest(){

    }

    /**
     * 设置闪屏界面的最大延迟跳转，让用户不至于在闪屏界面等待太久
     */
    fun delayToForward(){
         Thread(Runnable {
            GlobalUtil.sleep(MAX_WAIT_TIME.toLong())
             forwardToNextActivity(false,null)
         })
    }

    /**
     * 跳转到下一个activity,如果在闪屏界面停留的时间还不足规定最短停留时间，则会在这里等待一会，
     * 保证闪屏界面不至于一闪而过
     */
    @Synchronized
    open fun forwardToNextActivity(hasNewVersion:Boolean,version: Version?){

        if (!isForwarding){//如果正在跳转或已经跳转到下一个界面，则不再重复执行跳转
            isForwarding=true
            val currentTime=System.currentTimeMillis()
            val timeSpent=currentTime-enterTime
            if (timeSpent< MIN_WAIT_TIME){
                GlobalUtil.sleep(MIN_WAIT_TIME-timeSpent)
            }
            runOnUiThread {

                if (GifFun.isLogin()){
                    //开启主页
                    finish()
                }else{
                    if (isActive){

                    }else{

                    }
                }
            }
        }
    }
    companion object{
        private const val TAG="SplashActivity"

        /**
         * 应用程序在闪屏界面最短的停留时间
         */
        const val MIN_WAIT_TIME=2000

        /**
         * 应用程序在闪屏界面最长的停留时间
         */
        const val MAX_WAIT_TIME=5000
    }
}