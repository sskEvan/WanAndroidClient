package com.ssk.wanandroid.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Created by shisenkun on 2019-07-15.
 */
class CollectDotsView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    companion object {
        //粒子个数
        private val DOTS_COUNT = 6
        //粒子位置角度
        private val DOTS_POSITION_ANGLE = 360 / DOTS_COUNT
        private val DOTS_COLOR = Color.parseColor("#ED5E32")

        fun mapValueFromRangeToRange(
            value: Double,
            fromLow: Double,
            fromHigh: Double,
            toLow: Double,
            toHigh: Double
        ): Double {
            return toLow + (value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow)
        }
    }

    private var mCurrentProgress = 0f
    private var mCenterX: Int = 0
    private var mCenterY: Int = 0
    private var mMaxDotSize: Float = 0.toFloat()
    private var mCurrentRadius = 0f
    private var mCurrentDotSize = 0f
    private val mDotPaint = Paint()
    private var mMaxDotsRadius: Float = 0.toFloat()

    init {
        initView()
    }

    private fun initView() {
        mDotPaint.setStyle(Paint.Style.FILL)
        mDotPaint.setColor(DOTS_COLOR)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w / 2
        mCenterY = h / 2
        //粒子半径
        mMaxDotSize = 4f
        //最大半径
        mMaxDotsRadius = w / 2 - mMaxDotSize
    }

    override fun onDraw(canvas: Canvas) {
        drawOuterDotsFrame(canvas)
    }

    private fun drawOuterDotsFrame(canvas: Canvas) {
        for (i in 0 until DOTS_COUNT) {
            val cX = (mCenterX + mCurrentRadius * Math.cos(i.toDouble() * DOTS_POSITION_ANGLE.toDouble() * Math.PI / 180)).toFloat()
            val cY = (mCenterY + mCurrentRadius * Math.sin(i.toDouble() * DOTS_POSITION_ANGLE.toDouble() * Math.PI / 180)).toFloat()
            canvas.drawCircle(cX, cY, mCurrentDotSize, mDotPaint)
        }
    }

    fun setCurrentProgress(currentProgress: Float) {
        this.mCurrentProgress = currentProgress
        updateOuterDotsPosition()
        postInvalidate()
    }

    private fun updateOuterDotsPosition() {
        if (mCurrentProgress < 0.3f) {
            this.mCurrentRadius = mapValueFromRangeToRange(
                mCurrentProgress.toDouble(),
                0.0,
                0.3,
                0.0,
                (mMaxDotsRadius * 0.8f).toDouble()
            ).toFloat()
        } else {
            this.mCurrentRadius = mapValueFromRangeToRange(
                mCurrentProgress.toDouble(),
                0.3,
                1.0,
                (0.8f * mMaxDotsRadius).toDouble(),
                mMaxDotsRadius.toDouble()
            ).toFloat()
        }

        if (mCurrentProgress < 0.7) {
            this.mCurrentDotSize = mMaxDotSize
        } else {
            this.mCurrentDotSize =
                mapValueFromRangeToRange(mCurrentProgress.toDouble(), 0.7, 1.0, mMaxDotSize.toDouble(), 0.0).toFloat()
        }
    }

}
