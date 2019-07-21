package com.ssk.wanandroid.view.dialog

import android.content.Context
import android.graphics.drawable.ColorDrawable
import com.ssk.wanandroid.R
import kotlinx.android.synthetic.main.dialog_common_confirm.*

/**
 * Created by shisenkun on 2019-07-20.
 */
class CommonConfirmDialog(context: Context, val message: String) : BaseDialog(context) {

    private lateinit var mConfirmListener: () -> Unit?

    override fun getLayoutId() = R.layout.dialog_common_confirm

    override fun initView() {
        super.initView()

        window?.setBackgroundDrawable(ColorDrawable())
        tvMessage.text = message

        tvCancel.setOnClickListener {
            dismiss()
        }
        tvConfirm.setOnClickListener {
            mConfirmListener()
            dismiss()
        }
    }

    fun setConfirmListener(listener: () -> Unit): CommonConfirmDialog {
        this.mConfirmListener = listener
        return this
    }

}