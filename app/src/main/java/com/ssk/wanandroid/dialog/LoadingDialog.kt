package com.ssk.wanandroid.dialog

import android.content.Context
import android.view.View.GONE
import android.view.View.VISIBLE
import com.ssk.wanandroid.R
import com.ssk.wanandroid.widget.LoadingView
import kotlinx.android.synthetic.main.dialog_loading.*
import kotlinx.android.synthetic.main.dialog_loading.loadingView

/**
 * Created by shisenkun on 2019-06-22.
 */
class LoadingDialog(context: Context, var message: String = "") : BaseDialog(context) {

    override fun getLayoutId() = R.layout.dialog_loading

    override fun initView() {
        super.initView()
        loadingView.mLoadingAnimListener = object : LoadingView.LoadingAnimListenerAdapter() {
            override fun onLoadingStart() {
                refreshMessage()
            }

            override fun onSuccessAnimStart() {
                refreshMessage()
            }

            override fun onFailedAnimStart() {
                refreshMessage()
            }

            override fun onLoadingEnd() {
                dismiss()
            }
        }
    }

    override fun show() {
        super.show()
        loadingView.startRotateAnim()
    }

    override fun dismiss() {
        loadingView.stopAnim()
        super.dismiss()
    }

    fun refreshMessage() {
        if(message.isEmpty()) {
            tvMessage.visibility = GONE
        }else {
            tvMessage.visibility = VISIBLE
            tvMessage.setText(message)
        }
    }

    fun dismissFailed(failMessage: String) {
        this.message = failMessage
        loadingView.startFailedAnim()
    }

    fun dismissSuccess(successMessage: String) {
        this.message = successMessage
        loadingView.startSuccessAnim()
    }

}