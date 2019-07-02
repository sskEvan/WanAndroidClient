package com.ssk.wanandroid.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import com.ssk.wanandroid.R
import com.ssk.wanandroid.utils.dp2px
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by shisenkun on 2019-06-21.
 *
 */
class LoadingView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var mWidth = 0
    private var mHeight = 0
    private var mRotationRadius = dp2px(20f)
    private var mDotRadius = dp2px(5f)
    //动画加载完成后,包裹Path的圆的半径
    private var mPathWrapperCircleRadius = dp2px(18f);
    private var mBgColor = Color.TRANSPARENT
    private val mRotationDuration = 1000L
    //最小旋转动画时间, 可能刚开始要旋转,加载结果就来了，这是直接切换成其它动画,过度不自然
    private var mMinRotateDuration = 400L
    private val mScaleDuration = 250L
    private val mPathDuration = 600L
    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mDiagonalDist = 0f

    private var mDotMiddleColor1 = Color.parseColor("#44BBBB")
    private var mDotMiddleColor2 = Color.parseColor("#5577FF")
    private var mDotMiddleColor3 = Color.parseColor("#F350D2")
    private var mDotMiddleColor4 = Color.parseColor("#FF9600")
    private var mDotMiddleColor6 = Color.parseColor("#FF0000")
    private var mDotMiddleColor5 = Color.parseColor("#58AE6B")
    var mSuccessPathWrapperCircleColor = Color.parseColor("#58AE6B")
    var mFailedPathWrapperCircleColor = Color.parseColor("#FF0000")
    var mSuccessPathColor = Color.parseColor("#FFFFFF")
    var mFailedPathColor = Color.parseColor("#FFFFFF")

    private var mAnimator: ValueAnimator? = null
    var mLoadingAnimListener: LoadingAnimListener? = null

    private var mDotColors: IntArray = IntArray(6)
    private var mState: BaseState? = null
    private val mDotsPaint = Paint()
    private val mBgPaint = Paint()
    private val mPathPaint = Paint()
    private val mPathWrapperPaint = Paint()

    //当前旋转角度(弧度)
    private var mCurrentRotationAngle = 0f
    //当前旋转的半径
    private var mCurrentRotationRadius = mRotationRadius
    //当前包裹Path的圆的半径
    private var mCurrentPathWrapperCircleRadius = mDotRadius
    //当前圆点的透明度
    private var mCurrentDotAlpha = 255;

    //标记是否加载成功
    private var mIsLoadingSuccess = false

    private var mRotateAnimStartTime = 0L

    init {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.LoadingView)
            mRotationRadius = a.getDimensionPixelOffset(R.styleable.LoadingView_rotation_radius, mRotationRadius)
            mDotRadius = a.getDimensionPixelOffset(R.styleable.LoadingView_dot_radius, mDotRadius)
            mPathWrapperCircleRadius =
                a.getDimensionPixelOffset(R.styleable.LoadingView_path_wrapper_circle_radius, mPathWrapperCircleRadius)
            mBgColor = a.getColor(R.styleable.LoadingView_bg_color, mBgColor)
            mDotMiddleColor1 = a.getColor(R.styleable.LoadingView_dot_middle_color_1, mDotMiddleColor1)
            mDotMiddleColor2 = a.getColor(R.styleable.LoadingView_dot_middle_color_2, mDotMiddleColor2)
            mDotMiddleColor3 = a.getColor(R.styleable.LoadingView_dot_middle_color_3, mDotMiddleColor3)
            mDotMiddleColor4 = a.getColor(R.styleable.LoadingView_dot_middle_color_4, mDotMiddleColor4)
            mDotMiddleColor5 = a.getColor(R.styleable.LoadingView_dot_middle_color_5, mDotMiddleColor5)
            mDotMiddleColor6 = a.getColor(R.styleable.LoadingView_dot_middle_color_6, mDotMiddleColor6)
            mSuccessPathWrapperCircleColor = a.getColor(R.styleable.LoadingView_success_path_wrapper_circle_color, mSuccessPathWrapperCircleColor)
            mFailedPathWrapperCircleColor = a.getColor(R.styleable.LoadingView_failed_path_wrapper_circle_color, mFailedPathWrapperCircleColor)
            mSuccessPathColor = a.getColor(R.styleable.LoadingView_success_path_color, mSuccessPathColor)
            mFailedPathColor = a.getColor(R.styleable.LoadingView_failed_path_color, mFailedPathColor)

            a.recycle()
        }

        mDotColors[0] = mDotMiddleColor1
        mDotColors[1] = mDotMiddleColor2
        mDotColors[2] = mDotMiddleColor3
        mDotColors[3] = mDotMiddleColor4
        mDotColors[4] = mDotMiddleColor5
        mDotColors[5] = mDotMiddleColor6

        mDotsPaint.isAntiAlias = true
        mBgPaint.isAntiAlias = true
        mBgPaint.style = Paint.Style.FILL
        mBgPaint.color = mBgColor
        mPathPaint.isAntiAlias = true
        mPathPaint.style = Paint.Style.STROKE
        mPathPaint.strokeWidth = dp2px(2f).toFloat()
        mPathPaint.strokeCap = Paint.Cap.ROUND
        mPathWrapperPaint.isAntiAlias = true
        mPathWrapperPaint.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        mWidth = if (widthMode == MeasureSpec.EXACTLY) widthSize else (mRotationRadius + mDotRadius) * 2
        mHeight = if (heightMode == MeasureSpec.EXACTLY) heightSize else (mRotationRadius + mDotRadius) * 2

        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w / 2f
        mCenterY = h / 2f
        //勾股定律
        mDiagonalDist = Math.sqrt((w * w + h * h).toDouble()).toFloat() / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mState?.drawState(canvas)
    }

    private fun drawDots(canvas: Canvas) {
        //每个小圆之间的间隔角度 = 2π/小圆的个数
        val rotationAngle = (2 * Math.PI / mDotColors.size).toFloat()
        for (i in mDotColors.indices) {
            /**
             * x = r*cos(a) +centerX
             * y=  r*sin(a) + centerY
             * 每个小圆i*间隔角度 + 旋转的角度 = 当前小圆的真实角度
             */
            val angle = (i * rotationAngle + mCurrentRotationAngle).toDouble()
            val cx = (mCurrentRotationRadius * Math.cos(angle) + mCenterX).toFloat()
            val cy = (mCurrentRotationRadius * Math.sin(angle) + mCenterY).toFloat()
            mDotsPaint.color = mDotColors[i]
            mDotsPaint.alpha = mCurrentDotAlpha
            canvas.drawCircle(cx, cy, mDotRadius.toFloat(), mDotsPaint)
        }
    }

    private fun drawPathWrapperCircle(canvas: Canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mCurrentPathWrapperCircleRadius.toFloat(), mPathWrapperPaint)
    }

    private fun drawPath(paths: List<Path>, canvas: Canvas) {
        paths.forEach {
            canvas.drawPath(it, mPathPaint)
        }
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawColor(mBgColor)
    }

    fun updateCurrentRotationRadius(percent: Float) {
        if(mState == null) {
            mState = PullingState()
        }
        mCurrentDotAlpha = (Math.min(percent, 1f) * 255).toInt()
        mCurrentRotationRadius = (mRotationRadius * percent).toInt()
        Log.e("CommonRefreshHeader", "onMoving mCurrentRotationRadius=$mCurrentRotationRadius,percent=$percent")
        invalidate()
    }

    fun startRotateAnim() {
        mCurrentDotAlpha  = 255
        mCurrentRotationAngle = 0f
        mCurrentRotationRadius = mRotationRadius
        mCurrentPathWrapperCircleRadius = mDotRadius
        mIsLoadingSuccess = false
        mRotateAnimStartTime = Date().time

        mState?.cancel()
        mState = NormalRotateState()
        invalidate()
    }

    fun startSuccessAnim() {
        val currentTime = Date().time
        mPathWrapperPaint.color = mSuccessPathWrapperCircleColor
        mPathPaint.color = mSuccessPathColor
        postDelayed(
            {
                mState?.cancel()
                mIsLoadingSuccess = true
                mState = NarrowRotateState()
                invalidate()
            },
            Math.max(0, mMinRotateDuration + mRotateAnimStartTime - currentTime)
        )
    }

    fun startFailedAnim() {
        val currentTime = Date().time
        mPathWrapperPaint.color = mFailedPathWrapperCircleColor
        mPathPaint.color = mFailedPathColor
        postDelayed(
            {
                mState?.cancel()
                mIsLoadingSuccess = false
                mState = NarrowRotateState()
                invalidate()
            },
            Math.max(0, mMinRotateDuration + mRotateAnimStartTime - currentTime)
        )

    }

    fun stopAnim() {
        mAnimator?.cancel()
    }

    fun stopAnimAfterMinRotateDuration() {
        val currentTime = Date().time
        postDelayed(
            {
                mAnimator?.cancel()
                mLoadingAnimListener?.onLoadingCancelAfterMinRotateDuration()
            },
            Math.max(0, mMinRotateDuration + mRotateAnimStartTime - currentTime)
        )
    }


    private abstract inner class BaseState {
        abstract fun drawState(canvas: Canvas)
        fun cancel() {
            mAnimator?.cancel()
        }
    }

    /**
     * 正常旋转动画
     */
    private inner class NormalRotateState : BaseState() {
        init {
            mAnimator = ValueAnimator.ofFloat(0f, Math.PI.toFloat() * 2)
            mAnimator!!.setInterpolator(LinearInterpolator())
            mAnimator!!.addUpdateListener { valueAnimator ->
                //计算某个时刻当前的大圆旋转了的角度是多少
                mCurrentRotationAngle = valueAnimator.animatedValue as Float
                postInvalidate()
            }
            mAnimator!!.setDuration(mRotationDuration)
            mAnimator!!.setRepeatCount(ValueAnimator.INFINITE)
            mAnimator!!.start()
            mLoadingAnimListener?.onLoadingStart()
        }

        override fun drawState(canvas: Canvas) {
            drawBackground(canvas)
            drawDots(canvas)
        }
    }

    /**
     * 将圆点集合逐渐靠近中心点旋转
     */
    private inner class NarrowRotateState : BaseState() {
        init {
            mAnimator = ValueAnimator.ofFloat(mRotationRadius.toFloat(), 0f)
            mAnimator!!.setDuration(mScaleDuration)
            mAnimator!!.addUpdateListener { valueAnimator ->
                //某个时刻当前的大圆半径是多少
                mCurrentRotationRadius = (valueAnimator.animatedValue as Float).toInt()
                invalidate()
            }
            mAnimator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    mState = PathWrapperCircleScaleState()
                }
            })
            mAnimator!!.start()
        }

        override fun drawState(canvas: Canvas) {
            drawBackground(canvas)
            drawDots(canvas)
        }
    }

    /**
     * 将包裹Path的圆逐渐放大到动画
     */
    private inner class PathWrapperCircleScaleState : BaseState() {
        init {
            mAnimator = ValueAnimator.ofFloat(mDotRadius.toFloat(), mPathWrapperCircleRadius.toFloat())
            mAnimator!!.setDuration(mScaleDuration)
            mAnimator!!.addUpdateListener { valueAnimator ->
                mCurrentPathWrapperCircleRadius = (valueAnimator.animatedValue as Float).toInt()
                invalidate()
            }
            mAnimator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    mState = if (mIsLoadingSuccess) SuccessState() else FailedState()
                }
            })
            mAnimator!!.start()

        }

        override fun drawState(canvas: Canvas) {
            drawPathWrapperCircle(canvas)
        }
    }

    private inner class SuccessState : BaseState() {
        val successPathList = ArrayList<Path>()
        val successPath = Path()
        val animPath = Path()
        var currentPathLength = 0f
        var successPathMeasure: PathMeasure

        init {
            successPath.moveTo(mCenterX - mPathWrapperCircleRadius / 2, mCenterY);
            successPath.lineTo(mCenterX - mPathWrapperCircleRadius / 6, mCenterY + mPathWrapperCircleRadius / 3);
            successPath.lineTo(mCenterX + mPathWrapperCircleRadius / 2, mCenterY - mPathWrapperCircleRadius / 3);
            successPathList.add(animPath)
            successPathMeasure = PathMeasure(successPath, false)

            mAnimator = ValueAnimator.ofFloat(0f, 1.5f)
            mAnimator!!.setDuration(mPathDuration)
            mAnimator!!.addUpdateListener { valueAnimator ->
                currentPathLength = successPathMeasure.getLength() * Math.min(1f, valueAnimator.animatedValue as Float);
                successPathMeasure.getSegment(0f, currentPathLength, animPath, true);
                invalidate()
            }
            mAnimator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    mState = null
                    mLoadingAnimListener?.onLoadingEnd()
                }
            })
            mAnimator!!.start()
            mLoadingAnimListener?.onSuccessAnimStart()
        }

        override fun drawState(canvas: Canvas) {
            drawPathWrapperCircle(canvas)
            drawPath(successPathList, canvas)
        }
    }

    private inner class FailedState : BaseState() {
        val failedPathList = ArrayList<Path>()
        val failedPath1 = Path()
        val failedPath2 = Path()
        val animPath1 = Path()
        val animPath2 = Path()
        var currentPathLength = 0f
        var failedPathMeasure1: PathMeasure
        var failedPathMeasure2: PathMeasure

        init {
            failedPath1.moveTo(mCenterX - mPathWrapperCircleRadius / 3, mCenterY - mPathWrapperCircleRadius / 3);
            failedPath1.lineTo(mCenterX + mPathWrapperCircleRadius / 3, mCenterY + mPathWrapperCircleRadius / 3);
            failedPath2.moveTo(mCenterX + mPathWrapperCircleRadius / 3, mCenterY - mPathWrapperCircleRadius / 3);
            failedPath2.lineTo(mCenterX - mPathWrapperCircleRadius / 3, mCenterY + mPathWrapperCircleRadius / 3);
            failedPathList.add(animPath1)
            failedPathList.add(animPath2)
            failedPathMeasure1 = PathMeasure(failedPath1, false)
            failedPathMeasure2 = PathMeasure(failedPath2, false)

            mAnimator = ValueAnimator.ofFloat(0f, 1.5f)
            mAnimator!!.setDuration(mPathDuration)
            mAnimator!!.addUpdateListener { valueAnimator ->
                currentPathLength = failedPathMeasure1.getLength() * Math.min(1f, valueAnimator.animatedValue as Float);
                failedPathMeasure1.getSegment(0f, currentPathLength, animPath1, true);
                failedPathMeasure2.getSegment(0f, currentPathLength, animPath2, true);
                invalidate()
            }
            mAnimator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    mState = null
                    mLoadingAnimListener?.onLoadingEnd()
                }
            })
            mAnimator!!.start()
            mLoadingAnimListener?.onFailedAnimStart()
        }

        override fun drawState(canvas: Canvas) {
            drawPathWrapperCircle(canvas)
            drawPath(failedPathList, canvas)
        }
    }

    private inner class PullingState : BaseState() {
        override fun drawState(canvas: Canvas) {
            drawBackground(canvas)
            drawDots(canvas)
        }
    }

    interface LoadingAnimListener {
        fun onLoadingStart()
        fun onLoadingEnd()
        fun onFailedAnimStart()
        fun onSuccessAnimStart()
        fun onLoadingCancelAfterMinRotateDuration()
    }

    open class LoadingAnimListenerAdapter : LoadingAnimListener{
        override fun onLoadingEnd() {
        }

        override fun onFailedAnimStart() {
        }

        override fun onSuccessAnimStart() {
        }

        override fun onLoadingCancelAfterMinRotateDuration() {
        }

        override fun onLoadingStart() {
        }
    }

}