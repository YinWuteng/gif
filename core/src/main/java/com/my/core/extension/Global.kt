package com.my.core.extension

import android.annotation.SuppressLint
import android.os.Looper
import android.widget.Toast
import com.my.core.GifFun

/**
 * author:ywt
 * time:2019/6/24  20:47
 * desc:定义全局的扩展工具方法
 */

private var  toast:Toast?=null

/**
 * 用于在主线程中调用此方法，不在主线程中调用不会显示
 * @param  content 显示内容
 * @param  duration 显示时长
 */
@SuppressLint("ShowToast")
fun  showToast(content:String, duration:Int=Toast.LENGTH_SHORT){
    if (Looper.myLooper()== Looper.getMainLooper()){
        if (toast==null){
            toast= Toast.makeText(GifFun.getContext(),content,duration)
        }else{
            toast?.setText(content)
        }
        toast?.show()
    }
}

/**
 * 切换主线程后弹出Toast信息，此方法不管是在子线程还是主线程，都会弹出toast
 * @param content 显示内容
 * @param duration 显示时长
 */
@SuppressLint("ShowToast")
fun  showToastOnUiThread(content: String, duration: Int=Toast.LENGTH_SHORT){
    GifFun.getHandler().post {
        if (toast==null){
            toast= Toast.makeText(GifFun.getContext(),content,duration)
        }else{
            toast?.setText(content)
        }
        toast?.show()
    }
}