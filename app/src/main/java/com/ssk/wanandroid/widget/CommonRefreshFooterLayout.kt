package com.ssk.wanandroid.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.scwang.smartrefresh.layout.api.RefreshFooter
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.ssk.wanandroid.R
import kotlinx.android.synthetic.main.layout_common_refresh_header.view.loadingView
import kotlinx.android.synthetic.main.layout_common_refresh_header.view.tvMessage

/**
 * Created by shisenkun on 2019-06-26.
 */
class CommonRefreshFooterLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs), RefreshFooter {

    private val TAG = "CommonRefreshHeader"

    private val mFinishDuration = 500
    private var mRefreshKernel: RefreshKernel? = null
    private var mBackgroundColor = R.color.common_footer_bg
    private var mNoMoreData = false

    init {
        View.inflate(context, R.layout.layout_common_refresh_footer, this)
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }

    override fun getView(): View {
        return this
    }

    @SuppressLint("ResourceAsColor")
    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
        mRefreshKernel = kernel

        setPrimaryColors(ContextCompat.getColor(context, mBackgroundColor))
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        if(!mNoMoreData) {
            when (newState) {
                RefreshState.None, RefreshState.PullUpToLoad -> {
                    tvMessage.setText("上拉加载更多")
                }
                RefreshState.ReleaseToLoad -> {
                    tvMessage.setText("释放加载更多")
                }
                RefreshState.Loading -> {
                    tvMessage.setText("正在加载...")
                    if (loadingView.visibility != View.VISIBLE) loadingView.visibility = View.VISIBLE
                    loadingView.startRotateAnim()
                }
            }
        }
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        loadingView.stopAnim()
        if (success) {
            tvMessage.setText("加载成功")
            return 0;
        } else {
            tvMessage.setText("加载失败")
            return mFinishDuration;
        }
    }

    override fun setNoMoreData(noMoreData: Boolean): Boolean {
        mNoMoreData = noMoreData
        if (noMoreData) {
            tvMessage.setText("没有更多数据了")
            loadingView.visibility = View.INVISIBLE
        }
        return true
    }


    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
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
    }


    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

}