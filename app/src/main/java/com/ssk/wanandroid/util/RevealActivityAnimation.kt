package com.ssk.wanandroid.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator

class RevealActivityAnimation(private val mView: View, private val intent: Intent, private val mActivity: Activity) {

    private var revealX: Int = 0
    private var revealY: Int = 0
    private var duration: Int = 0

    fun revealActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
            intent.hasExtra(EXTRA_ACTIVITY_REVEAL_X) &&
            intent.hasExtra(EXTRA_ACTIVITY_REVEAL_Y)
        ) {
            mView.visibility = View.INVISIBLE

            revealX = intent.getIntExtra(EXTRA_ACTIVITY_REVEAL_X, 0)
            revealY = intent.getIntExtra(EXTRA_ACTIVITY_REVEAL_Y, 0)
            duration = intent.getIntExtra(EXTRA_ACTIVITY_REVEAL_DURATION, 0);

            val viewTreeObserver = mView.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val finalRadius = (Math.max(mView.width, mView.height) * 1.1).toFloat()

                        // create the animator for this view (the startCollectAnim radius is zero)
                        val circularReveal = ViewAnimationUtils.createCircularReveal(mView, revealX, revealY, 0f, finalRadius)
                        circularReveal.duration = duration.toLong()
                        circularReveal.interpolator = AccelerateInterpolator()

                        // make the view visible and startCollectAnim the animation
                        mView.visibility = View.VISIBLE
                        circularReveal.start()
                        mView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        } else {

            //if you are below android 5 it jist shows the activity
            mView.visibility = View.VISIBLE
        }
    }

    fun unRevealActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mActivity.finish()
        } else {
            val finalRadius = (Math.max(mView.width, mView.height) * 1.1).toFloat()
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                mView, revealX, revealY, finalRadius, 0f
            )

            circularReveal.duration = duration.toLong()
            circularReveal.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mView.visibility = View.INVISIBLE
                    mActivity.finish()
                    mActivity.overridePendingTransition(0, 0)
                }
            })

            circularReveal.start()
        }
    }

    companion object {
        val EXTRA_ACTIVITY_REVEAL_X = "EXTRA_ACTIVITY_REVEAL_X"
        val EXTRA_ACTIVITY_REVEAL_Y = "EXTRA_ACTIVITY_REVEAL_Y"
        val EXTRA_ACTIVITY_REVEAL_DURATION = "EXTRA_ACTIVITY_REVEAL_DURATION"
    }

}