package com.ssk.wanandroid.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.ssk.wanandroid.LoadingDialog
import com.ssk.wanandroid.R
import com.ssk.wanandroid.service.EventManager
import com.ssk.wanandroid.utils.AndroidVersion
import com.ssk.wanandroid.utils.DeviceInfo


/**
 * Created by shisenkun on 2019-06-18.
 */
abstract class BaseActivity : AppCompatActivity() {

    var mLoadingDialog: LoadingDialog? = null
    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
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
    }

    protected fun startActivity(clazz: Class<*>, bundle: Bundle) {
        startActivity(Intent(this, clazz).putExtras(bundle))
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_bottom_out)
    }

    abstract fun getLayoutId(): Int

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

    open fun doExitAnim() {
        overridePendingTransition(R.anim.slide_right_none, R.anim.slide_right_out)
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
            window.statusBarColor = resources.getColor(color)
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