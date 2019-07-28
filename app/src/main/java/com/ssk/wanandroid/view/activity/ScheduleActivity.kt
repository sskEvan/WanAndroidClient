package com.ssk.wanandroid.view.activity

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.BaseActivity
import com.ssk.wanandroid.view.adapter.SchedulePagerAdapter
import com.ssk.wanandroid.view.fragment.ScheduleFragment
import com.ssk.wanandroid.view.fragment.ScheduleFragment.Companion.TYPE_UNRESTRICTED
import com.ssk.wanandroid.view.fragment.ScheduleFragment.Companion.TYPE_WORK
import com.ssk.wanandroid.view.fragment.ScheduleFragment.Companion.TYPE_STUDY
import com.ssk.wanandroid.view.fragment.ScheduleFragment.Companion.TYPE_LIFE
import kotlinx.android.synthetic.main.activity_shedule.tabLayout
import kotlinx.android.synthetic.main.activity_shedule.viewPager

/**
 * Created by shisenkun on 2019-07-28.
 */
class ScheduleActivity : BaseActivity() {

    private val mUnrestrictedFragment by lazy { ScheduleFragment.create(TYPE_UNRESTRICTED) }
    private val mWorkFragment by lazy { ScheduleFragment.create(TYPE_WORK) }
    private val mStudyFragment by lazy { ScheduleFragment.create(TYPE_STUDY) }
    private val mLifeFragment by lazy { ScheduleFragment.create(TYPE_LIFE) }

    private lateinit var mPagerAdapter: SchedulePagerAdapter
    private var mCurrentPagerPosition = 0

    override fun getLayoutId() = R.layout.activity_shedule

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        setupToolbar(true)
        immersiveStatusBar(R.color.colorPrimary, true)

        setupViewPager()
    }

    fun setupViewPager() {
        tabLayout.setupWithViewPager(viewPager)
        mPagerAdapter = SchedulePagerAdapter(supportFragmentManager).apply {
            addFragment(mUnrestrictedFragment, "全部")
            addFragment(mWorkFragment, "工作")
            addFragment(mStudyFragment, "学习")
            addFragment(mLifeFragment, "生活")
        }

        viewPager.run {
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    mCurrentPagerPosition = position
                }

            })

            adapter = mPagerAdapter
            currentItem = mCurrentPagerPosition
            offscreenPageLimit = mPagerAdapter.count
        }
    }

}
