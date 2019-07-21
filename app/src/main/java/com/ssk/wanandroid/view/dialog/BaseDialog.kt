package com.ssk.wanandroid.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.ssk.wanandroid.R

/**
 * Created by shisenkun on 2019-06-22.
 */
abstract class BaseDialog(context: Context, style: Int = R.style.DialogStyle): Dialog(context, style) {

    protected lateinit var mContentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentView = View.inflate(context, getLayoutId(), null)
        setContentView(mContentView)
        setupDialogSize(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        initView()
    }

    open fun setupDialogSize(width: Int, height: Int) {
        val dialogWindow = this.window
        val p = dialogWindow!!.attributes
        p.width = width
        p.height = height
        dialogWindow.attributes = p
    }

    open fun initView() {

    }

    abstract fun getLayoutId(): Int

}