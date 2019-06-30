package com.ssk.wanandroid

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.ssk.wanandroid.base.BaseActivity
import com.ssk.wanandroid.utils.RevealActivityAnimation
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

        Handler().postDelayed({ presentActivity(ivLogo) }, 2500L)
    }

    fun presentActivity(view: View) {

        view.animate().scaleX(0f).scaleY(0f).alpha(0.5f).setDuration(150L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    val revealX = (view.x + view.width / 2).toInt()
                    val revealY = (view.y + view.height / 2).toInt()
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    intent.putExtra(RevealActivityAnimation.EXTRA_ACTIVITY_REVEAL_X, revealX)
                    intent.putExtra(RevealActivityAnimation.EXTRA_ACTIVITY_REVEAL_Y, revealY)
                    intent.putExtra(RevealActivityAnimation.EXTRA_ACTIVITY_REVEAL_DURATION, 600)
                    startActivity(intent)
                }
            })


        Handler().postDelayed({ finish() }, 1600)

    }


}