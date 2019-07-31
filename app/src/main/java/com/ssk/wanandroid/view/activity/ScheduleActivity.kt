package com.ssk.wanandroid.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.BaseActivity
import com.ssk.wanandroid.util.RevealActivityAnimation
import com.ssk.wanandroid.view.adapter.SchedulePagerAdapter
import com.ssk.wanandroid.view.fragment.ScheduleFragment
import com.ssk.wanandroid.view.fragment.ScheduleFragment.Companion.TYPE_UNRESTRICTED
import com.ssk.wanandroid.view.fragment.ScheduleFragment.Companion.TYPE_WORK
import com.ssk.wanandroid.view.fragment.ScheduleFragment.Companion.TYPE_STUDY
import com.ssk.wanandroid.view.fragment.ScheduleFragment.Companion.TYPE_LIFE
import kotlinx.android.synthetic.main.activity_shedule.*

/**
 * Created by shisenkun on 2019-07-28.
 */
class ScheduleActivity : BaseActivity() {

    companion object {
        const val STATUS_NOT_COMPLETED = 0
        const val STATUS_COMPLETED = 1
    }

    private val mUnrestrictedFragment by lazy { ScheduleFragment.create(TYPE_UNRESTRICTED, mStatus) }
    private val mWorkFragment by lazy { ScheduleFragment.create(TYPE_WORK, mStatus) }
    private val mStudyFragment by lazy { ScheduleFragment.create(TYPE_STUDY, mStatus) }
    private val mLifeFragment by lazy { ScheduleFragment.create(TYPE_LIFE, mStatus) }

    private lateinit var mPagerAdapter: SchedulePagerAdapter
    private var mCurrentPagerPosition = 0
    private var mStatus = STATUS_NOT_COMPLETED

    override fun getLayoutId() = R.layout.activity_shedule

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        mStatus = intent.extras.getInt("status")
        setupToolbar(true)
        immersiveStatusBar(R.color.colorPrimary, true)

        setupViewPager()

        if(mStatus == STATUS_NOT_COMPLETED) {
            toolbar?.title = "日程安排"
            fab.setOnClickListener {
                val revealX = (fab.x + fab.width / 2).toInt()
                val revealY = (fab.y + fab.height / 2).toInt()
                val intent = Intent(this@ScheduleActivity, AddScheduleActivity::class.java)
                intent.putExtra(RevealActivityAnimation.EXTRA_ACTIVITY_REVEAL_X, revealX)
                intent.putExtra(RevealActivityAnimation.EXTRA_ACTIVITY_REVEAL_Y, revealY)
                intent.putExtra(RevealActivityAnimation.EXTRA_ACTIVITY_REVEAL_DURATION, 600)
                intent.putExtra("scheduleType", mCurrentPagerPosition)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }else {
            toolbar?.title = "已完成日程"
            fab.hide()
        }
    }

    override fun onResume() {
        super.onResume()
        if(mStatus == STATUS_NOT_COMPLETED) {
            toolbar?.title = "日程安排"
        }else {
            toolbar?.title = "已完成日程"
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(mStatus == STATUS_NOT_COMPLETED) {
            menuInflater.inflate(R.menu.menu_schedule, menu)
            return true
        }else {
            return false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_item_schedule_completed -> {
                val bundle = Bundle()
                bundle.putInt("status", STATUS_COMPLETED)
                startActivity(ScheduleActivity::class.java, bundle)
            }
        }
        return true
    }

}
