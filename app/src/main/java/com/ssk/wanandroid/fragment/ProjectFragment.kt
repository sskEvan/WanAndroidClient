package com.ssk.wanandroid.fragment

import android.os.Bundle
import android.text.Html
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.WanFragment
import com.ssk.wanandroid.bean.ProjectTagVo
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.fragment.adapter.ProjectTagPagerAdapter
import com.ssk.wanandroid.viewmodel.ProjectViewModel
import kotlinx.android.synthetic.main.fragment_project.*
import kotlinx.android.synthetic.main.fragment_project.switchableConstraintLayout

/**
 * Created by shisenkun on 2019-06-23.
 */
class ProjectFragment : WanFragment<ProjectViewModel>() {

    companion object {
        fun create(): ProjectFragment {
            return ProjectFragment()
        }
    }

    private lateinit var mPagerAdapter: ProjectTagPagerAdapter
    private var mCurrentPagerPosition = 0

    override fun getLayoutResId() = R.layout.fragment_project

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser) {
            immersiveStatusBar(R.color.colorPrimary, true)
            animateToolbarTitle()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(false)
        toolbar?.title = "项目"
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
            mPagerAdapter.addFragment(ProjectDetailFragment.create(it.id), Html.fromHtml(it.name).toString())
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
        viewPager.currentItem = mCurrentPagerPosition;
    }

}