package com.ssk.wanandroid.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.ssk.lib_annotation.annotation.BindContentView
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.WanFragment
import com.ssk.wanandroid.bean.ProjectTagVo
import com.ssk.wanandroid.event.OnProjectFragmentFabClickResponseEvent
import com.ssk.wanandroid.event.OnProjectFragmentFabUpwardControlEvent
import com.ssk.wanandroid.event.OnProjectFragmentFabVisiableControlEvent
import com.ssk.wanandroid.view.adapter.ProjectTagPagerAdapter
import com.ssk.wanandroid.ext.fromHtml
import com.ssk.wanandroid.ext.logDebug
import com.ssk.wanandroid.ext.logWarn
import com.ssk.wanandroid.viewmodel.ProjectViewModel
import kotlinx.android.synthetic.main.fragment_project.*
import kotlinx.android.synthetic.main.fragment_project.switchableConstraintLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by shisenkun on 2019-06-23.
 */
@BindContentView(R.layout.fragment_project)
class ProjectFragment : WanFragment<ProjectViewModel>() {

    companion object {
        fun create(): ProjectFragment {
            return ProjectFragment()
        }
    }

    private lateinit var mPagerAdapter: ProjectTagPagerAdapter
    private var mCurrentPagerPosition = 0
    private var mIsFabUpward = true

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser) {
            immersiveStatusBar(R.color.colorPrimary, true)
            toolbar?.title = "项目"
            animateToolbarTitle()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(false)
        toolbar?.title = "项目"
        switchableConstraintLayout.setRetryListener {
            mViewModel.fetchProjectTagList()
        }

        fab.setOnClickListener {
            EventBus.getDefault().post(OnProjectFragmentFabClickResponseEvent(mIsFabUpward))
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mViewModel.fetchProjectTagList()
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mProjectTagVoList.observe(this@ProjectFragment, Observer {
                switchableConstraintLayout.switchSuccessLayout()
                setupViewPager(it)
            })

            mFetchProjectTagListErrorMsg.observe(this@ProjectFragment, Observer {
                switchableConstraintLayout.switchFailedLayout(it)
            })
        }
    }

    fun setupViewPager(projectTagVoList: List<ProjectTagVo>) {
        tabLayout.setupWithViewPager(viewPager)

        mPagerAdapter = ProjectTagPagerAdapter(childFragmentManager)
        projectTagVoList.forEach {
            mPagerAdapter.addFragment(ProjectDetailFragment.create(it.id), it.name)
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                mCurrentPagerPosition = position
            }

        })
        viewPager.adapter = mPagerAdapter
        viewPager.currentItem = mCurrentPagerPosition
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OnProjectFragmentFabVisiableControlEvent) {
        if(event.needShow) {
            if(!fab.isShown) fab.show()
        }else {
            if(fab.isShown) fab.hide()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OnProjectFragmentFabUpwardControlEvent) {
        mIsFabUpward = event.isFabUpward
        if(mIsFabUpward) {
            fab.animate().rotation(0f).start()  //箭头向上动画
        }else {
            fab.animate().rotation(180f).start()  //箭头向下动画
        }
    }

}