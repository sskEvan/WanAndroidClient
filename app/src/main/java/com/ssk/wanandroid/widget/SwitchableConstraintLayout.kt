package com.ssk.wanandroid.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.ssk.wanandroid.R
import kotlinx.android.synthetic.main.layout_switchable_constraint_layout_root.view.*

/**
 * Created by shisenkun on 2019-06-29.
 * 可切换状态的约束布局
 * 主要封装了切换加载中布局,正常布局,失败布局的功能
 */
open class SwitchableConstraintLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    companion object {
        private const val STATUS_LOADING = 0
        private const val STATUS_EMPTY = 1
        private const val STATUS_SUCCESS = 2
        private const val STATUS_FAILED = 3
    }

    private val mLoadingLayoutResIdArray = intArrayOf(R.id.loadingView)
    private val mFailedLayoutResIdArray = intArrayOf(R.id.ivFailed, R.id.tvFailedMsg, R.id.btnRetry)
    private val mEmptyLayoutResIdArray = intArrayOf(R.id.ivEmpty, R.id.tvEmpty)
    private lateinit var mRetryListener: () -> Unit?
    private var mStatus: Int = -1

    init {
        View.inflate(context, R.layout.layout_switchable_constraint_layout_root, this)
        initView()
    }

    private fun initView() {
        switchLoadingLayout()

        btnRetry.setOnClickListener {
            switchLoadingLayout();
            mRetryListener()
        }
    }

    fun switchSuccessLayout() {
        if (mStatus != STATUS_SUCCESS) {
            if (mStatus == STATUS_LOADING) {
                loadingView.mLoadingAnimListener = object : LoadingView.LoadingAnimListenerAdapter() {
                    override fun onLoadingCancelAfterMinRotateDuration() {
                        super.onLoadingCancelAfterMinRotateDuration()
                        showSuccessLayout()
                    }
                }
                loadingView.stopAnimAfterMinRotateDuration()
            } else {
                showSuccessLayout()
            }
        }
    }

    private fun showSuccessLayout() {
        mStatus = STATUS_SUCCESS
        for (i in 0..childCount - 1) {
            if (getChildAt(i).id in mLoadingLayoutResIdArray) {
                getChildAt(i).visibility = View.INVISIBLE
            } else if (getChildAt(i).id in mFailedLayoutResIdArray) {
                getChildAt(i).visibility = View.INVISIBLE
            } else if (getChildAt(i).id in mEmptyLayoutResIdArray) {
                getChildAt(i).visibility = View.INVISIBLE
            } else {
                getChildAt(i).visibility = View.VISIBLE
            }
        }
    }

    fun switchFailedLayout() {
        switchFailedLayout("")
    }

    fun switchFailedLayout(failedMsg: String) {
        if (mStatus != STATUS_FAILED) {
            if (failedMsg.isNotEmpty()) {
                tvFailedMsg.setText(failedMsg)
            }
            if (mStatus == STATUS_LOADING) {
                loadingView.mLoadingAnimListener = object : LoadingView.LoadingAnimListenerAdapter() {
                    override fun onLoadingCancelAfterMinRotateDuration() {
                        loadingView.stopAnim()
                        showFailedLayout()
                    }
                }
                loadingView.stopAnimAfterMinRotateDuration()
            } else {
                showFailedLayout()
            }
        }
    }

    private fun showFailedLayout() {
        mStatus = STATUS_FAILED
        for (i in 0..childCount - 1) {
            if (getChildAt(i).id in mFailedLayoutResIdArray) {
                getChildAt(i).visibility = View.VISIBLE
            } else {
                getChildAt(i).visibility = View.INVISIBLE
            }
        }
    }

    fun switchEmptyLayout() {
        if(mStatus != STATUS_EMPTY) {
            if (mStatus == STATUS_LOADING) {
                loadingView.mLoadingAnimListener = object : LoadingView.LoadingAnimListenerAdapter() {
                    override fun onLoadingCancelAfterMinRotateDuration() {
                        super.onLoadingCancelAfterMinRotateDuration()
                        showEmptyLayout()
                    }
                }
            } else {
                showEmptyLayout()
            }
        }
    }

    private fun showEmptyLayout() {
        mStatus = STATUS_EMPTY
        for (i in 0..childCount - 1) {
            if (getChildAt(i).id in mEmptyLayoutResIdArray) {
                getChildAt(i).visibility = View.VISIBLE
            } else {
                getChildAt(i).visibility = View.INVISIBLE
            }
        }
    }

    fun switchLoadingLayout() {
        if(mStatus != STATUS_LOADING) {
            mStatus = STATUS_LOADING
            for (i in 0..childCount - 1) {
                if (getChildAt(i).id in mLoadingLayoutResIdArray) {
                    getChildAt(i).visibility = View.VISIBLE
                } else {
                    getChildAt(i).visibility = View.INVISIBLE
                }
            }
            loadingView.startRotateAnim()
        }
    }

    fun setRetryListener(listener: () -> Unit) {
        this.mRetryListener = listener
    }

}