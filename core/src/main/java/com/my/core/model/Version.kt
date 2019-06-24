package com.my.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * author:ywt
 * time:2019/6/16  16:13
 * desc: 版本跟新实体类封装，如果存在版本更新则会提供下述信息
 */
@Parcelize
class Version(@SerializedName("change_log") val changeLog:String,
              @SerializedName("is_force") val isForce:Boolean,
              val url:String,
              @SerializedName("version_name") val versionName:String,
              @SerializedName("version_code") val versionCode:Int,
              val channdl:String):Parcelable {
}