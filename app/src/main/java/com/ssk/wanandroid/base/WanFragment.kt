package com.ssk.wanandroid.base

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.ssk.wanandroid.view.dialog.LoadingDialog
import com.ssk.wanandroid.R
import com.ssk.wanandroid.util.EventManager
import com.ssk.wanandroid.util.AndroidVersion
import com.ssk.wanandroid.util.DeviceInfo
import java.lang.reflect.ParameterizedType

/**
 * Created by shisenkun on 2019-06-24.
 */
abstract class WanFragment<VM: BaseViewModel> : Fragment() {

    lateinit var mContentView: View
    lateinit var mActivity: BaseActivity
    protected lateinit var mViewModel: VM;

    var toolbar: Toolbar? = null
    var mLoadingDialog: LoadingDialog? = null
    var mIsActivityCreate = false;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentView = inflater.inflate(getLayoutResId(), container, false)
        return mContentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mIsActivityCreate = true
        mActivity = activity as BaseActivity
        initVM()
        initView(savedInstanceState)
        initData(savedInstanceState)
        startObserve()
        EventManager.register(this)
    }

    override fun onResume() {
        super.onResume()
        EventManager.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventManager.unregister(this)
    }

    override fun onDestroyView() {
        mViewModel.let { lifecycle.removeObserver(it) }
        super.onDestroyView()
    }

    open fun initView(savedInstanceState: Bundle?) {}
    open fun initData(savedInstanceState: Bundle?) {}

    abstract fun getLayoutResId(): Int

    private fun initVM() {
        provideVMClass2()?.let {
            mViewModel = ViewModelProviders.of(this).get(it)
            mViewModel.let(lifecycle::addObserver)
        }
    }

    private fun provideVMClass2(): Class<VM>? = (javaClass.genericSuperclass as ParameterizedType).getActualTypeArguments()[0] as Class<VM>

    open fun startObserve() {}

    protected fun startActivity(clazz: Class<*>, doCommonEnterAnim: Boolean) {
        startActivity(Intent(mActivity, clazz))
        if(doCommonEnterAnim) {
            doEnterAnim()
        }
    }

    protected fun startActivity(clazz: Class<*>, bundle: Bundle, doCommonEnterAnim: Boolean) {
        startActivity(Intent(mActivity, clazz).putExtras(bundle))
        if(doCommonEnterAnim) {
            doEnterAnim()
        }
    }

    protected fun setupToolbar(displayHomeAsUpEnabled: Boolean) {
        if (mIsActivityCreate != false) {
            toolbar = mContentView.findViewById(R.id.toolbar)
            mActivity.setSupportActionBar(toolbar)
            val actionBar = mActivity.supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled)
            animateToolbarTitle()
        }
    }

    fun animateToolbarTitle() {
        val t = toolbar?.getChildAt(0)
        if (t != null && t is TextView) {
            t.alpha = 0f
            t.scaleX = 0.8f
            t.animate().alpha(1f).scaleX(1f).setDuration(300)
        }
    }

    open fun doEnterAnim() {
        mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    /**
     * 设置沉浸式状态栏。只适配Android 5.0以上系统的手机。
     * color 沉浸式颜色
     * handleLayoutOverLap 是否处理布局重叠问题
     */
    protected fun immersiveStatusBar(color: Int, handleLayoutOverLap: Boolean) {
        if (mIsActivityCreate && AndroidVersion.hasLollipop()) {
            val decorView = mActivity.window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            mActivity.window.statusBarColor = ContextCompat.getColor(mActivity, color)
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
        mLoadingDialog = LoadingDialog(mActivity, message)
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

    fun showSnackBar(msg: String) {
        val snackBar = Snackbar.make(mActivity.window.decorView, msg, Snackbar.LENGTH_SHORT)
        (snackBar.getView().findViewById(R.id.snackbar_text) as TextView).setTextColor(Color.WHITE)
        snackBar.show()
    }

}