package com.ssk.wanandroid.view.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.BaseActivity
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.view.fragment.HomeFragment
import com.ssk.wanandroid.view.fragment.KnowledgeFragment
import com.ssk.wanandroid.view.fragment.MineFragment
import com.ssk.wanandroid.view.fragment.ProjectFragment
import com.ssk.wanandroid.util.RevealActivityAnimation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private var mBackPressTime = 0L

    private val mFragmentList = arrayListOf<Fragment>()
    private val mHomeFragment by lazy { HomeFragment.create() }
    private val mKnowledgeFragment by lazy { KnowledgeFragment.create() }
    private val mProjectFragment by lazy { ProjectFragment.create() }
    private val mMineFragment by lazy { MineFragment.create() }

    override fun getLayoutId() = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        bottomNavigationView.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.item_navigation_home -> viewPager.setCurrentItem(0, false)
                    R.id.item_navigation_knowledge -> viewPager.setCurrentItem(1, false)
                    R.id.item_navigation_project -> viewPager.setCurrentItem(2, false)
                    R.id.item_navigation_mine -> viewPager.setCurrentItem(3, false)
                }
                return true
            }
        })

        if (savedInstanceState == null) {
            RevealActivityAnimation(clRoot, intent, this).revealActivity()
        }

        setupViewPager()
    }

    fun setupViewPager() {
        mFragmentList.add(mHomeFragment)
        mFragmentList.add(mKnowledgeFragment)
        mFragmentList.add(mProjectFragment)
        mFragmentList.add(mMineFragment)

        viewPager.offscreenPageLimit = mFragmentList.size
        viewPager.adapter = object : androidx.fragment.app.FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int) = mFragmentList[position]

            override fun getCount() = mFragmentList.size
        }
    }

    override fun onBackPressed() {
        val now = System.currentTimeMillis()
        if (now - mBackPressTime > 2000) {
            showToast("请再按一次退出应用")
            mBackPressTime = now
        } else {
            super.onBackPressed()
        }
    }

    override fun doExitAnim() {}

}
