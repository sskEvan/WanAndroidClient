package com.ssk.wanandroid.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


/**
 * Created by shisenkun on 2019-07-15.
 */
class CollectCircleView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    companion object {
        private val CIRCLE_COLOR = Color.parseColor("#ED5E32")
    }

    private var mTempBitmap: Bitmap? = null
    private var mTempCanvas: Canvas? = null
    private val mOuterPaint = Paint()
    private val mInnerPaint = Paint()
    private var mMaxCircleSize: Int = 0
    private var mOuterCircleRadiusProgress = 0f
    private var mInnerCircleRadiusProgress = 0f

    init {
        initView()
    }

    private fun initView() {
        //初始化画笔
        //外圆画笔样式为填充
        mOuterPaint.style = Paint.Style.FILL
        mOuterPaint.color = CIRCLE_COLOR
        //内圆图像混合模式之清除图像
        mInnerPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mMaxCircleSize = w / 2
        mTempBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
        mTempCanvas = Canvas(mTempBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mTempCanvas!!.drawColor(0xffffff, PorterDuff.Mode.CLEAR)
        //画外圆
        mTempCanvas!!.drawCircle(getWidth() / 2f, getHeight() / 2f, mOuterCircleRadiusProgress * mMaxCircleSize, mOuterPaint)
        //画内圆
        mTempCanvas!!.drawCircle(getWidth() / 2f, getHeight() / 2f, mInnerCircleRadiusProgress * mMaxCircleSize, mInnerPaint)
        canvas.drawBitmap(mTempBitmap, 0f, 0f, null)
    }

    //对外暴露的属性，设置进度，让圆心动态变化。
    fun setInnerCircleRadiusProgress(innerCircleRadiusProgress: Float) {
        this.mInnerCircleRadiusProgress = innerCircleRadiusProgress
        postInvalidate()
    }

    fun setOuterCircleRadiusProgress(outerCircleRadiusProgress: Float) {
        this.mOuterCircleRadiusProgress = outerCircleRadiusProgress
        postInvalidate()
    }

}