package com.my.gif.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_login.view.*
import java.util.jar.Attributes

/**
 * author:ywt
 * time:2019/6/24  22:06
 * desc:自定义登录界面Layout,监听布局高度的变化，如果高度比小于4:3说明此时键盘弹出，应改变布局的
 *      比例结果以保证所有元素都不会被键盘遮挡
 */
class LoginLayout(context: Context, attributes: AttributeSet) : LinearLayout(context, attributes) {
    var keyboardShowed = false
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed) {
            val width = right - left
            val height = bottom - top
            if (height.toFloat() / width.toFloat() < 4f / 3f) {//如果高宽比小于4:3，说明此时键盘弹出

                post {
                    loginBgWallLayout.visibility=View.VISIBLE
                    val params=loginLayoutTop.layoutParams as LayoutParams
                    params.weight=1.5f
                    keyboardShowed=true
                    loginLayoutTop.requestLayout()
                }
            } else {

                if (keyboardShowed){
                    post {
                        loginBgWallLayout.visibility=View.VISIBLE
                        val params=loginLayoutTop as LayoutParams
                        params.weight=6f
                        loginLayoutTop.requestLayout()
                    }
                }
            }
        }
    }
}