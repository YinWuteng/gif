package com.my.gif.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewStub
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toolbar
import com.my.gif.common.PermissionListener
import com.my.gif.common.RequestLifecycle
import com.my.gif.event.MessageEvent
import com.my.gif.util.ActivityCollector
import com.my.gif.util.AndroidVersion
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.lang.ref.WeakReference
/**
 * author:ywt
 * time:2019/6/14 20:59
 * desc:应用程序所有Activity的基类
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), RequestLifecycle {

    /**
     * 判断当前Activity是否在前台
     */
    protected var isActive: Boolean = false

    /**
     *当前Activity 实例
     */
    protected var activity: Activity? = null

    /**
     * Activity中显示加载等待的控件
     */
    protected var loading: ProgressBar? = null

    /**
     * Activity中由于服务器异常导致加载失败显示布局
     */
    protected var loadErrorView: View? = null

    /**
     * Activity中由于网络异常导致加载失败显示的布局
     */
    protected var badNetWorkView: View? = null

    /**
     * Activity中当界面上没有任何内容时展示的布局
     */
    private var noContentView: View? = null

    private var weakRefActivity: WeakReference<Activity>? = null

    var toolbar: Toolbar? = null

    private var progressDialog: ProgressDialog? = null


    private var mListener: PermissionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        weakRefActivity = WeakReference(this)
        ActivityCollector.add(weakRefActivity)
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        isActive = true
    }

    override fun onDestroy() {
        super.onDestroy()
        activity = null
        ActivityCollector.remove(weakRefActivity)
        EventBus.getDefault().unregister(this)
    }

    override fun onPause() {
        super.onPause()
        isActive = false
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupViews()
    }

    protected open fun setupViews() {
        //loading=findViewById(R.id.loading)
    }

    /**
     * 设置toolbar
     */
    protected open fun setupToolbar() {
        //toolbar=findViewById(R.id.toolbar)
        //setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * 将状态栏设置成透明，只适配5.0以上手机
     */
    protected fun transparentStatusBar() {
        if (AndroidVersion.hasLollipop()) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }
    }


    /**
     * 检查和处理运行时权限，并将用户授权的结果通过PermissionListener进行回调
     * @param permission 要检查和处理的运行时权限数组
     * @param listener 用于接收授权结果的监听器
     */
    protected fun handlePermissions(permissions: Array<String>?, listener: PermissionListener) {
        if (permissions == null || activity == null) {
            return
        }
        mListener = listener
        val requestPermissionList = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(activity!!, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionList.add(permission)
            }
        }

        if (!requestPermissionList.isEmpty()) {
            ActivityCompat.requestPermissions(activity!!, requestPermissionList.toTypedArray(), 1)
        } else {
            listener.onGranted()
        }
    }

    /**
     * 隐藏软键盘
     */
    fun hideSoftKeyboard() {
        try {
            val view = currentFocus
            if (view != null) {
                val binder = view.windowToken
                val manger = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manger.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 显示软键盘。
     */
    fun showSoftKeyboard(editText: EditText?) {
        try {
            if (editText != null) {
                editText.requestFocus()
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showSoftInput(editText, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 当Activity中的加载内容服务器返回失败，通过此方法显示提示界面给用户
     * @param tip
     *
     */
    protected fun showLoadErrorVew(tip: String) {
        if (loadErrorView != null) {
            loadErrorView?.visibility = View.VISIBLE
            return
        }
//        val  viewStub=findViewById<ViewStub>(R.id.loadErrorView)
//        if (viewStub!=null){
//            loadErrorView=viewStub.inflate()
//            val  loadErrortext=loadErrorView?.findViewById<TextView>(R.id.loadErrortext)
//            loadErrortext?.text=tip
//        }
    }

    /**
     * 当Activity中的内容因为网络原因无法显示的时候，通过此方法显示提示界面给用户
     * @param listener 重新加载点击事件回调
     */
    protected fun showBadNetworkView(listener: View.OnClickListener) {
        if (badNetWorkView != null) {
            badNetWorkView?.visibility = View.VISIBLE
            return
        }

        //val  viewStub=findViewById<ViewStub>(R.id.badNetWorkView)
        // if(viewStub!=null){
        //  badNetworkView =viewStub.inflate()
        //  val badNetworkRootView=badNetworkView?.findViewById<View>(R.id.badNetworkRootView)
        //  badNetworkRootView?.setOnClickListener(listener)
        // }
    }

    /**
     * 当Activity中没有任何内容的时候，通过此方法显示提示界面给用户
     * @param tip 界面中的提示信息
     */

    protected fun showNoContentView(tip: String) {
        if (noContentView != null) {
            noContentView?.visibility = View.VISIBLE
            return
        }
        // val  viewStub=findViewById<ViewStub>(R.id.noContentView)
    }

    /**
     * 将load error view进行隐藏
     */
    protected fun hideLoadErrorView() {
        loadErrorView?.visibility = View.GONE
    }

    /**
     * 将no content view 进行隐藏
     */
    protected fun hideNoContentView() {
        noContentView?.visibility = View.GONE
    }

    /**
     * 将bad network view进行隐藏
     */
    protected fun hideBadNetworkView() {
        badNetWorkView?.visibility = View.GONE
    }

    /**
     * 显示进度对话框
     */
    fun showProgressDialog(title: String?, message: String) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this).apply {
                if (title != null) {
                    setTitle(title)
                }
                setMessage(message)
                setCancelable(false)
            }
        }
        progressDialog?.show()
    }

    /**
     * 关闭进度对话框
     */
    fun closeProgressDialog() {
        progressDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(messageEvent: MessageEvent) {

    }

    /**
     * 动态权限
     */
    open fun permissionsGrant() {

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1-> if (grantResults.isNotEmpty()){
                val deniedPermissions=ArrayList<String>()
                for (i in grantResults.indices){
                    val grantResult=grantResults[i]
                    val  permission=permissions[i]
                    if (grantResult!=PackageManager.PERMISSION_GRANTED){
                        deniedPermissions.add(permission)
                    }
                }

                if (deniedPermissions.isEmpty()){
                    mListener!!.onGranted()
                }else{
                    mListener!!.onDenied(deniedPermissions)
                }
            }
            else->{

            }
        }

    }

    override fun startLoading() {
        loading?.visibility=View.VISIBLE
        hideBadNetworkView()
        hideNoContentView()
        hideLoadErrorView()
    }

    override fun loadFinished() {
        loading?.visibility=View.GONE
    }

    override fun loadFailed(msg: String?) {
        loading?.visibility=View.GONE
    }

    companion object{
        private const val TAG="BaseActivity"
    }
}