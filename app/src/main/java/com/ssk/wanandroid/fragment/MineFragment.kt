package com.ssk.wanandroid.fragment

import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import com.bumptech.glide.Glide
import com.ssk.wanandroid.LoginActivity
import com.ssk.wanandroid.R
import com.ssk.wanandroid.app.WanAndroid
import com.ssk.wanandroid.base.BaseFragment
import com.ssk.wanandroid.dialog.CommonConfirmDialog
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.utils.BlurTransformation
import com.ssk.wanandroid.utils.CropCircleTransformation
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * Created by shisenkun on 2019-06-23.
 */

class MineFragment : BaseFragment() {

    companion object {
        fun create(): MineFragment {
            return MineFragment()
        }
    }

    private var mLastTouchY = -1
    private var mIvHeadPortraitBgOriginalHeight = 0
    private var mRecoverIvHeadPortraitBgAnim: ValueAnimator? = null
    private val mScrollDamping = 0.25f

    override fun getLayoutResId() = R.layout.fragment_mine

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        ivHeadPortraitBg.post {
            mIvHeadPortraitBgOriginalHeight = ivHeadPortraitBg.height
        }

        nsvRoot.setOnTouchListener { _, event ->
            event?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mRecoverIvHeadPortraitBgAnim?.cancel()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if(mLastTouchY != -1) {
                            if (event.y - mLastTouchY > 5 && nsvRoot.scrollY == 0
                                && ivHeadPortraitBg.height <= mIvHeadPortraitBgOriginalHeight * 2
                            ) {  //向下滑动到顶了,并且背景图片高度不大于原始高度到2倍
                                ivHeadPortraitBg.layoutParams.height =
                                    (ivHeadPortraitBg.height + (event.y - mLastTouchY) * mScrollDamping).toInt()
                                ivHeadPortraitBg.requestLayout()
                            } else if (event.y - mLastTouchY < -5
                                && ivHeadPortraitBg.height - mIvHeadPortraitBgOriginalHeight > 0
                            ) {  //向上滑动，并且头像背景大于原始高度
                                ivHeadPortraitBg.layoutParams.height =
                                    (ivHeadPortraitBg.height + (event.y - mLastTouchY) * mScrollDamping).toInt()
                                ivHeadPortraitBg.requestLayout()
                            }
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (ivHeadPortraitBg.height - mIvHeadPortraitBgOriginalHeight > 0) {  //松手后，并且头像背景大于原始高度
                            startRecoverIvHeadPortraitBgAnim()
                        }
                    }
                }
                mLastTouchY = event.y.toInt()
            }

            false
        }

        ivHeadPortrait.setOnClickListener { onUserClick() }

        tvUserName.setOnClickListener { onUserClick() }

        srlSchedule.setOnClickListener { showToast("日程安排") }

        srlCollect.setOnClickListener { showToast("收藏") }

        srlAbout.setOnClickListener { showToast("关于") }

        srlAppInfo.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.parse("package:" + WanAndroid.appPackage)
            intent.data = uri
            startActivity(intent)
        }

        srlClearCache.setOnClickListener {
            showToast(
                "清除缓存"
            )
        }

        btnLogout.setOnClickListener {
            CommonConfirmDialog(mActivity, "您确定退出当前登陆?")
                .setConfirmListener {
                    WanAndroid.logout()
                    updateAccountUi()
                }
                .show()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            animateToolbarTitle()
            immersiveStatusBar(R.color.translucent, false)

            updateAccountUi()
        }
    }

    override fun onResume() {
        super.onResume()
        updateAccountUi()
    }

    private fun updateAccountUi() {
        if(WanAndroid.currentUser != null) {
            Glide.with(mActivity)
                .load(R.mipmap.ic_test_head_portrait)
                .bitmapTransform(CropCircleTransformation(mActivity))
                .into(ivHeadPortrait)
            Glide.with(mActivity)
                .load(R.mipmap.ic_test_head_portrait)
                .bitmapTransform(BlurTransformation(mActivity))
                .into(ivHeadPortraitBg)
            tvUserName.text = WanAndroid.currentUser!!.username
            btnLogout.visibility = View.VISIBLE
        }else {
            ivHeadPortrait.setImageResource(R.mipmap.ic_head_portrait_default)
            ivHeadPortraitBg.setImageResource(R.mipmap.ic_head_portrait_bg)
            tvUserName.text = "点击登陆"
            btnLogout.visibility = View.GONE
        }

    }

    private fun startRecoverIvHeadPortraitBgAnim() {
        mRecoverIvHeadPortraitBgAnim?.cancel()
        mRecoverIvHeadPortraitBgAnim = ValueAnimator.ofInt(ivHeadPortraitBg.height, mIvHeadPortraitBgOriginalHeight)
        mRecoverIvHeadPortraitBgAnim!!.addUpdateListener { animation ->
            ivHeadPortraitBg.layoutParams.height = animation!!.animatedValue as Int
            ivHeadPortraitBg.requestLayout()
        }
        mRecoverIvHeadPortraitBgAnim!!.start()
    }

    private fun onUserClick() {
        if (WanAndroid.currentUser == null) {
            startActivity(LoginActivity::class.java, false)
            mActivity.overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_none)
        }
    }

}