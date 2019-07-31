package com.ssk.wanandroid.view.dialog

import android.content.Context
import android.graphics.drawable.ColorDrawable
import com.ssk.wanandroid.R
import com.ssk.wanandroid.bean.ScheduleVo
import kotlinx.android.synthetic.main.dialog_schedule_operation.*

/**
 * Created by shisenkun on 2019-07-30.
 */
class ScheduleOperationDialog(context: Context, val scheduleVo: ScheduleVo) : BaseDialog(context) {

    var mSheduleLister: ScheduleOperationListener? = null

    override fun getLayoutId() = R.layout.dialog_schedule_operation

    override fun initView() {
        super.initView()

        tvComplete.text = if(scheduleVo.status == 1) {  //原来已完成->未完成
            "未完成"
        } else {
            "完成"
        }

        window?.setBackgroundDrawable(ColorDrawable())

        tvComplete.setOnClickListener {
            mSheduleLister?.onComplete(scheduleVo)
        }
        tvDelete.setOnClickListener {
            mSheduleLister?.onDelete(scheduleVo)
        }

        tvCancel.setOnClickListener {
            dismiss()
        }
    }

    interface ScheduleOperationListener {
        fun onComplete(scheduleVo: ScheduleVo)
        fun onDelete(scheduleVo: ScheduleVo)
    }

}