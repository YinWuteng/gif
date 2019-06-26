package com.my.gif.common

import android.annotation.TargetApi
import android.os.Build
import android.transition.Transition

/**
 * author:ywt
 * time:2019/6/26  20:36
 * desc:动画监听类
 */
//兼容19以上
@TargetApi(Build.VERSION_CODES.KITKAT)
open class SimpleTransitionListener :Transition.TransitionListener{
    override fun onTransitionEnd(transition: Transition?) {
   }

    override fun onTransitionResume(transition: Transition?) {
   }

    override fun onTransitionPause(transition: Transition?) {
   }

    override fun onTransitionCancel(transition: Transition?) {
   }

    override fun onTransitionStart(transition: Transition?) {
  }
}