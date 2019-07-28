package com.ssk.wanandroid.view.fragment

import android.os.Bundle
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_schedule.*

/**
 * Created by shisenkun on 2019-07-28.
 */
class ScheduleFragment : BaseFragment() {

    companion object {
        const val TYPE_UNRESTRICTED = 0
        const val TYPE_WORK = 1
        const val TYPE_STUDY = 2
        const val TYPE_LIFE = 3

        fun create(type: Int): ScheduleFragment = ScheduleFragment().apply {
            val bundle = Bundle()
            bundle.putInt("type", type)
            arguments = bundle
        }
    }

    private var mType: Int = 0

    override fun getLayoutResId() = R.layout.fragment_schedule

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }

}