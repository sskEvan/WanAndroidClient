package com.ssk.wanandroid.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.BaseActivity
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.util.RevealActivityAnimation
import com.ssk.wanandroid.view.adapter.SchedulePagerAdapter
import com.ssk.wanandroid.view.fragment.ScheduleTodoFragment
import com.ssk.wanandroid.view.fragment.ScheduleTodoFragment.Companion.TYPE_UNRESTRICTED
import com.ssk.wanandroid.view.fragment.ScheduleTodoFragment.Companion.TYPE_WORK
import com.ssk.wanandroid.view.fragment.ScheduleTodoFragment.Companion.TYPE_STUDY
import com.ssk.wanandroid.view.fragment.ScheduleTodoFragment.Companion.TYPE_LIFE
import kotlinx.android.synthetic.main.activity_shedule.*

/**
 * Created by shisenkun on 2019-07-28.
 */
class ScheduleActivity : BaseActivity() {

    private val mUnrestrictedFragment by lazy { ScheduleTodoFragment.create(TYPE_UNRESTRICTED) }
    private val mWorkFragment by lazy { ScheduleTodoFragment.create(TYPE_WORK) }
    private val mStudyFragment by lazy { ScheduleTodoFragment.create(TYPE_STUDY) }
    private val mLifeFragment by lazy { ScheduleTodoFragment.create(TYPE_LIFE) }

    private lateinit var mPagerAdapter: SchedulePagerAdapter
    private var mCurrentPagerPosition = 0

    override fun getLayoutId() = R.layout.activity_shedule

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        setupToolbar(true)
        immersiveStatusBar(R.color.colorPrimary, true)

        setupViewPager()

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
        menuInflater.inflate(R.menu.menu_schedule, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_item_schedule_completed -> {
                showToast("已完成")
            }
        }
        return true
    }

}
