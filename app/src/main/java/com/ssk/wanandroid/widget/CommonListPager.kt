package com.ssk.wanandroid.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ssk.wanandroid.R
import com.ssk.wanandroid.util.dp2px
import kotlinx.android.synthetic.main.pager_common_list.view.*

/**
 * Created by shisenkun on 2019-07-21.
 * 封装通用列表页面
 */
class CommonListPager<T>
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) :
    SwitchableConstraintLayout(context, attrs) {

    private var mIsRefreshing = false
    private var mIsLoadingMore = false
    private lateinit var rvLayoutManager: LinearLayoutManager
    private var mIsFabUpward = true
    private var mCurrentPage = 0
    private lateinit var mAdapter: BaseQuickAdapter<T, out BaseViewHolder>
    private var mCommonfab: FloatingActionButton? = null
    private var mNoMoreData = false

    private lateinit var mRootView: View
    private var mAddCommonFab = true
    private var mAddCommonHeader = true
    private var mAddCommonFooter = true

    var commonListPagerListener: CommonListPagerListener? = null

    init {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        mRootView = View.inflate(context, R.layout.pager_common_list, this)
        //FloatingActionButton在此处通过kotlin-android-extensions方式找到的话，会包NPE,这里通过findViewById查找
        // mCommonfab = rootView.findViewById(R.id.floatingActionButton)

        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.CommonListPager)
            mAddCommonFab = a.getBoolean(R.styleable.CommonListPager_add_common_fab, true)
            mAddCommonHeader = a.getBoolean(R.styleable.CommonListPager_add_common_header, true)
            mAddCommonFooter = a.getBoolean(R.styleable.CommonListPager_add_common_footer, true)
        }

        if (mAddCommonFab) {
            mCommonfab = FloatingActionButton(context)
            mCommonfab!!.let {
                (mRootView as ViewGroup).addView(it)
                it.setImageResource(R.mipmap.ic_upward)
                val lp = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                lp.bottomToBottom = LayoutParams.PARENT_ID
                lp.rightToRight = LayoutParams.PARENT_ID
                lp.setMargins(dp2px(16f), dp2px(16f), dp2px(16f), dp2px(16f))
                it.layoutParams = lp
            }
        }
    }

    fun setAdapter(baseQuickAdapter: BaseQuickAdapter<T, out BaseViewHolder>) {

        mAdapter = baseQuickAdapter

        rvLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        recyclerView.run {
            layoutManager = rvLayoutManager
            adapter = mAdapter

            mCommonfab?.let {
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    @SuppressLint("RestrictedApi")
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val firstCompletelyVisibleIndex = rvLayoutManager.findFirstCompletelyVisibleItemPosition()

                        if (firstCompletelyVisibleIndex == 0 && it.isShown) {
                            it.hide()
                        } else if (firstCompletelyVisibleIndex > 0) {
                            if (!it.isShown) {
                                it.show()
                            }
                            val lastCompletelyVisibleIndex = rvLayoutManager.findLastCompletelyVisibleItemPosition()
                            if (lastCompletelyVisibleIndex == mAdapter.data.size + mAdapter.headerLayoutCount - 1 && mNoMoreData) {
                                it.hide()
                            } else {
                                if (dy > 0) {  //向上滑动
                                    if (firstCompletelyVisibleIndex > 0) {
                                        if (mIsFabUpward) {
                                            mIsFabUpward = false
                                            it.animate().rotation(180f).start()  //箭头向下动画
                                        }
                                    }
                                } else if (dy < 0) { //向下滑动
                                    if (lastCompletelyVisibleIndex < mAdapter.itemCount) {
                                        if (!mIsFabUpward) {
                                            mIsFabUpward = true
                                            it.animate().rotation(0f).start()  //箭头向上动画
                                        }
                                    }
                                }
                            }
                        }
                    }
                })

                it.setOnClickListener {
                    if (mIsFabUpward) {
                        recyclerView.smoothScrollToPosition(0)
                    } else {
                        recyclerView.smoothScrollToPosition(mAdapter.data.size)
                    }
                }
            }
        }

        if (mAddCommonHeader) {
            val commonRefreshHeaderLayout = CommonRefreshHeaderLayout(context)
            commonRefreshHeaderLayout.setPrimaryColors(ContextCompat.getColor(context, R.color.colorPrimary))
            smartRefreshLayout.setRefreshHeader(commonRefreshHeaderLayout)

            smartRefreshLayout.setOnRefreshListener {
                mCurrentPage = 0
                mIsRefreshing = true

                commonListPagerListener?.refresh()
            }
        } else {
            smartRefreshLayout.setEnableRefresh(false)
        }

        if (mAddCommonFooter) {
            smartRefreshLayout.setRefreshFooter(CommonRefreshFooterLayout(context))

            smartRefreshLayout.setOnLoadMoreListener {
                ++mCurrentPage
                mIsLoadingMore = true

                commonListPagerListener?.loadMore(mCurrentPage)
            }
        } else {
            smartRefreshLayout.setEnableLoadMore(false)
        }

        setRetryListener {
            mCurrentPage = 0
            mIsRefreshing = false
            mIsLoadingMore = false
            commonListPagerListener?.retry()
        }
    }

    fun addData(newData: Collection<T>) {
        if (mIsRefreshing) {
            smartRefreshLayout.finishRefresh(true)
            mIsRefreshing = false
        } else if (mIsLoadingMore) {
            smartRefreshLayout.finishLoadMore(true)
            mIsLoadingMore = false
        } else {
            switchSuccessLayout()
            mCommonfab?.hide()
        }

        if (mCurrentPage == 0) {
            if (mAdapter.data is MutableList<*>) {
                mAdapter.data.clear()
            }
        }

        mAdapter.addData(newData)

        if (mAdapter.data.size == 0) {
            switchEmptyLayout()
        }
    }

    fun removeItem(position: Int) {
        mAdapter.data.removeAt(position)
        mAdapter.notifyItemRemoved(position)
        if (mAdapter.data.size == 0) {
            switchEmptyLayout()
        }
    }

    fun fetchDataError(errorMessage: String) {
        if (!mIsRefreshing && !mIsLoadingMore) {
            switchFailedLayout(errorMessage)
        }

        if (mIsRefreshing) {
            smartRefreshLayout.finishRefresh(false)
            mIsRefreshing = false
        }
        if (mIsLoadingMore) {
            smartRefreshLayout.finishLoadMore(false)
            mIsLoadingMore = false
        }
    }

    fun setNoMoreData(noMoreData: Boolean) {
        mNoMoreData = noMoreData
        smartRefreshLayout.setNoMoreData(mNoMoreData)
    }

    fun showLoading() {
        switchLoadingLayout()
    }

    fun getRecyclerView(): RecyclerView {
        return recyclerView
    }

    fun getLayoutManager(): LinearLayoutManager {
        return rvLayoutManager
    }

    interface CommonListPagerListener {
        fun retry()
        fun refresh()
        fun loadMore(page: Int)
    }

}