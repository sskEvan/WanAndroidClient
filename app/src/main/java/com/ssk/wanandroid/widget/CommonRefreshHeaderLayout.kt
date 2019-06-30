package com.ssk.wanandroid.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.ssk.wanandroid.R
import kotlinx.android.synthetic.main.layout_common_refresh_header.view.*

/**
 * Created by shisenkun on 2019-06-26.
 */
class CommonRefreshHeaderLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs), RefreshHeader {

    private val TAG = "CommonRefreshHeader"

    private var mRefreshKernel: RefreshKernel? = null
    private val mFinishDuration = 500
    private var mBackgroundColor = R.color.colorPrimary
    private var isRefreshing = false;

    init {
        View.inflate(context, R.layout.layout_common_refresh_header, this)
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }

    override fun getView(): View {
        return this
    }

    @SuppressLint("ResourceAsColor")
    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
        Log.d(TAG, "onInitialized")
        mRefreshKernel = kernel
        mRefreshKernel?.requestDrawBackgroundFor(this, mBackgroundColor)
        setBackgroundColor(mBackgroundColor)
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        Log.e(TAG, "onStateChanged newState=$newState")
        when (newState) {
            RefreshState.None, RefreshState.PullDownToRefresh -> {
                isRefreshing = false
                tvMessage.setText("下拉刷新")
            }
            RefreshState.ReleaseToRefresh -> {
                isRefreshing = false
                tvMessage.setText("释放刷新")
            }
            RefreshState.Refreshing -> {
                isRefreshing = true
                tvMessage.setText("正在刷新...")
                loadingView.startRotateAnim()
            }
            else -> {
                isRefreshing = false
            }
        }
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        Log.e(TAG, "onFinish success=$success")
        loadingView.stopAnim()
        if (success) {
            tvMessage.setText("刷新成功")
        } else {
            tvMessage.setText("刷新失败")
        }
        return mFinishDuration;  //延迟500毫秒之后再弹回
    }


    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        Log.e(TAG, "onReleased height=$height,maxDragHeight=$maxDragHeight")
    }


    @SuppressLint("ResourceAsColor")
    override fun setPrimaryColors(vararg colors: Int) {
        if (colors.size > 0) {
            mBackgroundColor = colors[0]
            mRefreshKernel?.requestDrawBackgroundFor(this, mBackgroundColor)
            setBackgroundColor(mBackgroundColor)
        }

    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        Log.d(TAG, "onStartAnimator height=$height,maxDragHeight=$maxDragHeight")
    }


    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
        Log.d(TAG, "onMoving isDragging=$isDragging,percent=$percent")
        if (!isRefreshing && percent <= 1.3) {
            loadingView.updateCurrentRotationRadius(percent)
        }
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

}