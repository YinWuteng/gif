package com.my.gif.util

import android.app.Activity
import java.lang.ref.WeakReference

/**
 * author:ywt
 * time:2019/6/14 21:12
 * desc:Activity管理器，可以用于一键杀死所有Activity
 */
object ActivityCollector {
    private const val TAG="ActivityCollector"

    private val activityList= ArrayList<WeakReference<Activity>?>()

    fun size():Int{
        return activityList.size
    }

    fun add(weakRefActivity: WeakReference<Activity>?){
        activityList.add(weakRefActivity)
    }

    fun remove(weakRefActivity: WeakReference<Activity>?){
        val  result= activityList.remove(weakRefActivity)

    }
    /**
     * 结束所有activity
     */
    fun finishAll(){
        if (activityList.isNotEmpty()){
            for (activityWeakReference in activityList){
                val  activity=activityWeakReference?.get()
                if (activity!=null && !activity.isFinishing){
                    activity.finish()
                }
            }
            activityList.clear()
        }
    }
}