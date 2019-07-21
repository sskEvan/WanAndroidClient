package com.ssk.wanandroid.view.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import com.ssk.wanandroid.R
import com.ssk.wanandroid.app.WanAndroid
import com.ssk.wanandroid.base.BaseActivity
import com.ssk.wanandroid.util.RevealActivityAnimation
import kotlinx.android.synthetic.main.activity_splash.*

/**
 * Created by shisenkun on 2019-06-23.
 */
class SplashActivity : BaseActivity() {

    override fun getLayoutId() = R.layout.activity_splash

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        immersiveStatusBar(R.color.colorPrimary, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        WanAndroid.uiHandler.postDelayed({ forwardMainActivity() }, 1500L)
    }

    fun forwardMainActivity() {

        ivFont.pivotX = ivFont.width / 2F
        ivFont.pivotY = 0F
        ivFont.animate().scaleX(0F).scaleY(0F).alpha(0.5F).setDuration(150L).start()

        ivLogo.pivotX = ivLogo.width / 2F
        ivLogo.pivotY = ivLogo.height.toFloat()
        ivLogo.animate().scaleX(0F).scaleY(0F).alpha(0.5F).setDuration(150L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                  super.onAnimationEnd(animation)
                    val revealX = (ivLogo.x + ivLogo.width / 2).toInt()
                    val revealY = ivLogo.y.toInt()
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    intent.putExtra(RevealActivityAnimation.EXTRA_ACTIVITY_REVEAL_X, revealX)
                    intent.putExtra(RevealActivityAnimation.EXTRA_ACTIVITY_REVEAL_Y, revealY)
                    intent.putExtra(RevealActivityAnimation.EXTRA_ACTIVITY_REVEAL_DURATION, 1000)
                    startActivity(intent)
                }
            })
            .start()

        WanAndroid.uiHandler.postDelayed({ finish() }, 2500)
    }

}