package com.ssk.wanandroid.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.ssk.lib_annotation.processor.ViewBinder
import com.ssk.wanandroid.view.dialog.LoadingDialog
import com.ssk.wanandroid.R
import com.ssk.wanandroid.util.EventManager
import com.ssk.wanandroid.util.AndroidVersion
import com.ssk.wanandroid.util.DeviceInfo


/**
 * Created by shisenkun on 2019-06-18.
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    var mLoadingDialog: LoadingDialog? = null
    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewBinder.injectActivity(this)
        initView(savedInstanceState)
        initData(savedInstanceState)
        EventManager.register(this)
    }

    override fun onDestroy() {
        EventManager.unregister(this)
        super.onDestroy()
    }

    open fun initView(savedInstanceState: Bundle?) {}
    open fun initData(savedInstanceState: Bundle?) {}

    protected fun startActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
        doEnterAnim()
    }

    protected fun startActivity(clazz: Class<*>, bundle: Bundle) {
        startActivity(Intent(this, clazz).putExtras(bundle))
        doEnterAnim()
    }

    protected fun setupToolbar(displayHomeAsUpEnabled: Boolean) {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar!!.setNavigationOnClickListener{onBackPressed()}
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled)
        animateToolbarTitle()
    }

    fun animateToolbarTitle() {
        val t = toolbar?.getChildAt(0)
        if (t != null && t is TextView) {
            t.alpha = 0f
            t.scaleX = 0.8f
            t.animate().alpha(1f).scaleX(1f).setDuration(300)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        doExitAnim()
    }

    open fun doEnterAnim() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    open fun doExitAnim() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    /**
     * 设置沉浸式状态栏。只适配Android 5.0以上系统的手机。
     * color 沉浸式颜色
     * handleLayoutOverLap 是否处理布局重叠问题
     */
    protected fun immersiveStatusBar(color: Int, handleLayoutOverLap: Boolean) {
        if (AndroidVersion.hasLollipop()) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = ContextCompat.getColor(this, color)
            val contentView: ViewGroup = decorView.findViewById(android.R.id.content)
            contentView.setPadding(
                decorView.paddingLeft,
                if(handleLayoutOverLap) DeviceInfo.statusBarHeight else 0,
                decorView.paddingRight,
                decorView.paddingBottom
            )
        }
    }

    fun showLoadingDialog(message: String = "") {
        dismissLoadingDialog()
        mLoadingDialog = LoadingDialog(this, message)
        mLoadingDialog!!.show()
    }

    fun dismissLoadingDialogFailed(message: String = "") {
        mLoadingDialog?.dismissFailed(message)
    }

    fun dismissLoadingDialogSuccess(message: String = "") {
        mLoadingDialog?.dismissSuccess(message)
    }

    fun dismissLoadingDialog() {
        mLoadingDialog?.dismiss()
        mLoadingDialog = null
    }

}