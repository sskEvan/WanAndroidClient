package com.ssk.wanandroid.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.ssk.wanandroid.LoginActivity
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.BaseFragment
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.service.AccountManager
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

    private var mLastTouchY = 0f
    private var mIvHeadPortraitBgOriginalHeight = 0;
    private var mRecoverIvHeadPortraitBgAnim: ValueAnimator? = null
    private val mScrollDamping = 0.25f

    override fun getLayoutResId() = R.layout.fragment_mine

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        ivHeadPortraitBg.post {
            mIvHeadPortraitBgOriginalHeight = ivHeadPortraitBg.height
        }

        nsvRoot.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                event?.let {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            mRecoverIvHeadPortraitBgAnim?.cancel()
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (event.y - mLastTouchY > 5 && nsvRoot.scrollY == 0
                                && ivHeadPortraitBg.height <= mIvHeadPortraitBgOriginalHeight * 2
                            ) {  //向下滑动到顶了,并且背景图片高度不大于原始高度到2倍
                                ivHeadPortraitBg.getLayoutParams().height =
                                    (ivHeadPortraitBg.getHeight() + (event.y - mLastTouchY) * mScrollDamping).toInt()
                                ivHeadPortraitBg.requestLayout()
                            } else if (event.y - mLastTouchY < -5
                                && ivHeadPortraitBg.height - mIvHeadPortraitBgOriginalHeight > 0
                            ) {  //向上滑动，并且头像背景大于原始高度
                                ivHeadPortraitBg.getLayoutParams().height =
                                    (ivHeadPortraitBg.getHeight() + (event.y - mLastTouchY) * mScrollDamping).toInt()
                                ivHeadPortraitBg.requestLayout()
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            if (ivHeadPortraitBg.height - mIvHeadPortraitBgOriginalHeight > 0) {  //松手后，并且头像背景大于原始高度
                                startRecoverIvHeadPortraitBgAnim()
                            }
                        }
                    }
                    mLastTouchY = event.y
                }

                return false;
            }
        })

        ivHeadPortrait.setOnClickListener { onUserClick() }
        tvUserName.setOnClickListener { onUserClick() }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            animateToolbarTitle()
            immersiveStatusBar(R.color.translucent, false)
        }
    }

    override fun onResume() {
        super.onResume()
        tvUserName.setText(if(AccountManager.isLogin) AccountManager.currentUser!!.username else "点击登陆")
    }

    fun startRecoverIvHeadPortraitBgAnim() {
        mRecoverIvHeadPortraitBgAnim?.cancel()
        mRecoverIvHeadPortraitBgAnim = ValueAnimator.ofInt(ivHeadPortraitBg.height, mIvHeadPortraitBgOriginalHeight)
        mRecoverIvHeadPortraitBgAnim!!.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                ivHeadPortraitBg.getLayoutParams().height = animation!!.animatedValue as Int
                ivHeadPortraitBg.requestLayout()
            }

        })
        mRecoverIvHeadPortraitBgAnim!!.start()
    }

    fun onUserClick() {
        if(AccountManager.isLogin) {
            showToast("更换头像...")
        }else {
            startActivity(LoginActivity::class.java)
            mActivity.overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_none);
        }

    }

}