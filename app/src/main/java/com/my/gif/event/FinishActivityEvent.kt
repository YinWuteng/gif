package com.my.gif.event

/**
 * author:ywt
 * time:2019/6/16 14:39
 * desc:销毁Activity的事件消息
 */
class FinishActivityEvent:MessageEvent() {
    var activityClass:Class<*>?=null
}