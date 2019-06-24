package com.my.gif.common

/**
 * author:ywt
 * time:2019/6/14 21:09
 * desc:动态权限是否被允许
 */
interface PermissionListener {

    fun onGranted()
    fun onDenied(deniedPermission: List<String>)
}