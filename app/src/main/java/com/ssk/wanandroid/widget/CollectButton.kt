package com.ssk.wanandroid.widget

import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.OvershootInterpolator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.ssk.wanandroid.R
import kotlinx.android.synthetic.main.layout_collect_button.view.*


/**
 * Created by shisenkun on 2019-07-15.
 */
class CollectButton
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    companion object {
        private val DECCELERATE_INTERPOLATOR = DecelerateInterpolator()
        private val ACCELERATE_DECELERATE_INTERPOLATOR = AccelerateDecelerateInterpolator()
        private val OVERSHOOT_INTERPOLATOR = OvershootInterpolator(4f)
    }

    private var mIsChecked = true
    private var mAnimatorSet: AnimatorSet? = null

   init {
       LayoutInflater.from(context).inflate(R.layout.layout_collect_button, this, true)
   }

    fun startCollectAnim() {
        setEnabled(false)
        ivCollect.setImageResource(R.mipmap.ic_collected)
        ivCollect.animate().cancel()
        ivCollect.setScaleX(0F)
        ivCollect.setScaleY(0F)
        vCircle.setInnerCircleRadiusProgress(0f)
        vCircle.setOuterCircleRadiusProgress(0f)
        vDotsView.setCurrentProgress(0f)

        mAnimatorSet = AnimatorSet()

        val outerCircleAnimator = ObjectAnimator.ofFloat(vCircle, "outerCircleRadiusProgress", 0f, 1f)
        outerCircleAnimator.duration = 550
        outerCircleAnimator.startDelay = 0
        outerCircleAnimator.interpolator = DECCELERATE_INTERPOLATOR
        //目标属性的属性名、初始值或结束值
        val innerCircleAnimator = ObjectAnimator.ofFloat(vCircle, "innerCircleRadiusProgress", 0f, 1f)
        innerCircleAnimator.duration = 650
        innerCircleAnimator.startDelay = 0
        innerCircleAnimator.interpolator = DECCELERATE_INTERPOLATOR

        val starScaleYAnimator = ObjectAnimator.ofFloat(ivCollect, ImageView.SCALE_Y, 0.2f, 1f)
        starScaleYAnimator.duration = 550
        starScaleYAnimator.startDelay = 0
        starScaleYAnimator.interpolator = OVERSHOOT_INTERPOLATOR

        val starScaleXAnimator = ObjectAnimator.ofFloat(ivCollect, ImageView.SCALE_X, 0.2f, 1f)
        starScaleXAnimator.duration = 550
        starScaleXAnimator.startDelay = 0
        starScaleXAnimator.interpolator = OVERSHOOT_INTERPOLATOR

        val dotsAnimator = ObjectAnimator.ofFloat(vDotsView, "currentProgress", 0f, 1f)
        dotsAnimator.setDuration(850)
        dotsAnimator.setStartDelay(100)
        dotsAnimator.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR)

        mAnimatorSet!!.playTogether(
            outerCircleAnimator,
            innerCircleAnimator,
            starScaleYAnimator,
            starScaleXAnimator,
            dotsAnimator
        )

        mAnimatorSet!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                setEnabled(true)
            }
        })


        mAnimatorSet!!.start()
    }

    fun startUncollectAnim() {
        setEnabled(false)
        ivCollect.setImageResource(R.mipmap.ic_uncollect)
        ivCollect.animate().cancel()
        ivCollect.setScaleX(0F)
        ivCollect.setScaleY(0F)
        ivCollect.animate().cancel()

        mAnimatorSet = AnimatorSet()
        val starScaleYAnimator = ObjectAnimator.ofFloat(ivCollect, ImageView.SCALE_Y, 0.2f, 1f)
        starScaleYAnimator.duration = 550
        starScaleYAnimator.startDelay = 0
        starScaleYAnimator.interpolator = OVERSHOOT_INTERPOLATOR

        val starScaleXAnimator = ObjectAnimator.ofFloat(ivCollect, ImageView.SCALE_X, 0.2f, 1f)
        starScaleXAnimator.duration = 550
        starScaleXAnimator.startDelay = 0
        starScaleXAnimator.interpolator = OVERSHOOT_INTERPOLATOR

        mAnimatorSet!!.playTogether(starScaleYAnimator, starScaleXAnimator)

        mAnimatorSet!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                setEnabled(true)
            }
        })


        mAnimatorSet!!.start()
    }

    fun setChecked(isChecked: Boolean) {
        mIsChecked = isChecked
        if(mIsChecked) {
            ivCollect.setImageResource(R.mipmap.ic_collected)
        }else {
            ivCollect.setImageResource(R.mipmap.ic_uncollect)
        }
    }

}
