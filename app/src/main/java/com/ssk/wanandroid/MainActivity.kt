package com.ssk.wanandroid

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ssk.wanandroid.base.BaseActivity
import com.ssk.wanandroid.fragment.HomeFragment
import com.ssk.wanandroid.fragment.KnowledgeFragment
import com.ssk.wanandroid.fragment.MineFragment
import com.ssk.wanandroid.fragment.ProjectFragment
import com.ssk.wanandroid.utils.AndroidVersion
import com.ssk.wanandroid.utils.RevealActivityAnimation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

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

        initViewPager()
    }

    fun initViewPager() {
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

}
