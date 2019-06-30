package com.ssk.wanandroid.fragment

import android.os.Bundle
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.BaseFragment

/**
 * Created by shisenkun on 2019-06-23.
 */
class ProjectFragment : BaseFragment() {

    companion object {
        fun create(): ProjectFragment {
            return ProjectFragment()
        }
    }

    override fun getLayoutResId() = R.layout.fragment_project

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(false)
        toolbar?.title = "项目"
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser) {
            immersiveStatusBar(R.color.colorPrimary, true)
            animateToolbarTitle()
        }
    }

}