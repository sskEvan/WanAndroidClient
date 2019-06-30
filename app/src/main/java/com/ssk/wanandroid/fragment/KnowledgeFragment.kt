package com.ssk.wanandroid.fragment

import android.os.Bundle
import android.util.Log
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.BaseFragment

/**
 * Created by shisenkun on 2019-06-23.
 */

class KnowledgeFragment : BaseFragment() {

    companion object {
        fun create() : KnowledgeFragment {
            return KnowledgeFragment()
        }
    }

    override fun getLayoutResId() = R.layout.fragment_knowledge

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(false)
        toolbar?.title = "知识体系"
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser) {
            immersiveStatusBar(R.color.colorPrimary, true)
            animateToolbarTitle()
        }
    }

}