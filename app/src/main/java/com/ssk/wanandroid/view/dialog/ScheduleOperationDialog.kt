package com.ssk.wanandroid.view.dialog

import android.content.Context
import android.graphics.drawable.ColorDrawable
import com.ssk.wanandroid.R
import com.ssk.wanandroid.bean.TodoVo
import kotlinx.android.synthetic.main.dialog_schedule_operation.*

/**
 * Created by shisenkun on 2019-07-30.
 */
class ScheduleOperationDialog(context: Context, val todoVo: TodoVo) : BaseDialog(context) {

    var mSheduleLister: ScheduleOperationListener? = null

    override fun getLayoutId() = R.layout.dialog_schedule_operation

    override fun initView() {
        super.initView()

        window?.setBackgroundDrawable(ColorDrawable())

        tvComplete.setOnClickListener {
            mSheduleLister?.onComplete(todoVo)
        }
        tvDelete.setOnClickListener {
            mSheduleLister?.onDelete(todoVo)
        }

        tvCancel.setOnClickListener {
            dismiss()
        }
    }

    interface ScheduleOperationListener {
        fun onComplete(todoVo: TodoVo)
        fun onDelete(todoVo: TodoVo)
    }

}