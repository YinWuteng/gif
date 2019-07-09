package com.my.gif.ui

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.my.core.Const
import com.my.core.GifFun
import com.my.core.GlobalUtil
import com.my.core.SharedUtil
import com.my.core.extension.logDebug
import com.my.gif.R
import com.my.gif.base.BaseActivity
import org.litepal.LitePal
import org.litepal.LitePalDB

class MainActivity : BaseActivity() {

    internal var isNeedToRefresh = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIsNeedToRefresh()
        initDatabase()
        setContentView(R.layout.activity_main)

    }

    override fun setupViews() {
        setupToolbar()
    }
    /**
     * 检查是否需要刷新
     */
    private fun checkIsNeedToRefresh() {
        val autoRefresh = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
            GlobalUtil.getString(R.string.key_auto_refresh), true
        )
        if (autoRefresh) {
            val lastUseTime = SharedUtil.read(Const.Feed.MAIN_LAST_USE_TIME, 0L)
            val timeNotUsed = System.currentTimeMillis() - lastUseTime
            logDebug(TAG, "not used for " + timeNotUsed / 1000 + " seconds")
            if (timeNotUsed > 10 * 60 * 1000) {//超过10分钟未使用
                isNeedToRefresh = true
            }
        }
    }

    /**
     * 初始化数据库
     */
    private fun initDatabase() {
        val litepalDB = LitePalDB.fromDefault("gif_" + GifFun.getUserId().toString())
        LitePal.use(litepalDB)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_SEARCH = 10000
        fun actionStart(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
