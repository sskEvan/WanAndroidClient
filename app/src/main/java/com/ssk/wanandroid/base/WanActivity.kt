package com.ssk.wanandroid.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.ssk.lib_annotation.processor.ViewBinder
import com.ssk.wanandroid.view.dialog.LoadingDialog
import com.ssk.wanandroid.R
import com.ssk.wanandroid.util.EventManager
import com.ssk.wanandroid.util.AndroidVersion
import com.ssk.wanandroid.util.DeviceInfo
import java.lang.reflect.ParameterizedType


/**
 * Created by shisenkun on 2019-06-18.
 */
@SuppressLint("Registered")
open class WanActivity<VM: BaseViewModel> : AppCompatActivity() {

    var mLoadingDialog: LoadingDialog? = null
    var toolbar: Toolbar? = null
    protected lateinit var mViewModel: VM;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewBinder.injectActivity(this)
        EventManager.register(this)
        initVM()
        initView(savedInstanceState)
        initData(savedInstanceState)
        startObserve()
    }

    override fun onDestroy() {
        EventManager.unregister(this)
        mViewModel.let { lifecycle.removeObserver(it) }
        super.onDestroy()
    }

    private fun initVM() {
        provideVMClass2()?.let {
            mViewModel = ViewModelProviders.of(this).get(it)
            mViewModel.let(lifecycle::addObserver)
        }
    }

    private fun provideVMClass2(): Class<VM>? = (javaClass.genericSuperclass as ParameterizedType).getActualTypeArguments()[0] as Class<VM>

    open fun startObserve() {}

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
        toolbar!!.setNavigationOnClickListener{
            onBackPressed()
        }
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

    protected fun showSnackBar(msg: String) {
        val snackBar = Snackbar.make(window.decorView, msg, Snackbar.LENGTH_SHORT)
        (snackBar.getView().findViewById(R.id.snackbar_text) as TextView).setTextColor(Color.WHITE)
        snackBar.show()
    }

    fun hideSoftKeyboard() {
        try {
            val view = currentFocus
            if (view != null) {
                val binder = view.windowToken
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        } catch (e: Exception) {

        }
    }

    fun showSoftKeyboard(editText: EditText?) {
        try {
            if (editText != null) {
                editText.requestFocus()
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showSoftInput(editText, 0)
            }
        } catch (e: Exception) {
        }
    }

}