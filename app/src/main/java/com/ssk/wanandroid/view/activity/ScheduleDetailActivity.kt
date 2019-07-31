package com.ssk.wanandroid.view.activity

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.BaseActivity
import com.ssk.wanandroid.bean.ScheduleTypeVo
import com.ssk.wanandroid.bean.ScheduleVo
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_schedule_detail.*

/**
 * Created by shisenkun on 2019-07-29.
 */
class ScheduleDetailActivity : BaseActivity() {

    override fun getLayoutId() = R.layout.activity_schedule_detail

    private lateinit var scheduleVo: ScheduleVo

    @RequiresApi(Build.VERSION_CODES.N)
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        setupToolbar(true)
        immersiveStatusBar(R.color.colorPrimary, true)

        scheduleVo = intent.extras.getSerializable("scheduleVo") as ScheduleVo

        tvContent.setText(scheduleVo.content)
        tvTitle.setText(scheduleVo.title)
        tvDate.text = scheduleVo.dateStr
        if(scheduleVo.priority == 1) {
            rbPriority.text = "重要"
        }else {
            rbPriority.text = "一般"
        }
        when(scheduleVo.type) {
            1 -> tvType.text = "工作"
            2 -> tvType.text = "学习"
            3 -> tvType.text = "生活"
            else -> tvType.text = "无"
        }
    }

}