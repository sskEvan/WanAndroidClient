package com.ssk.wanandroid.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ssk.wanandroid.R
import com.ssk.wanandroid.ext.logDebug
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
    private var fab: FloatingActionButton
    private var mShowFab = true
    private var mNoMoreData = false

    var commonListPagerListener: CommonListPagerListener? = null

    init {
        val rootView = View.inflate(context, R.layout.pager_common_list, this)
        //FloatingActionButton在此处通过kotlin-android-extensions方式找到的话，会包NPE,这里通过findViewById查找
        fab = rootView.findViewById(R.id.floatingActionButton)
    }

    fun setAdapter(baseQuickAdapter: BaseQuickAdapter<T, out BaseViewHolder>) {

        mAdapter = baseQuickAdapter

        rvLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        recyclerView.run {
            layoutManager = rvLayoutManager
            adapter = mAdapter

            if(mShowFab) {
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    @SuppressLint("RestrictedApi")
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val firstCompletelyVisibleIndex = rvLayoutManager.findFirstCompletelyVisibleItemPosition()

                        if (firstCompletelyVisibleIndex == 0 && fab.isShown) {
                            fab.hide()
                        } else if(firstCompletelyVisibleIndex > 0) {
                            if (!fab.isShown) {
                                fab.show()
                            }
                            val lastCompletelyVisibleIndex = rvLayoutManager.findLastCompletelyVisibleItemPosition()
                            if(lastCompletelyVisibleIndex == mAdapter.data.size + mAdapter.headerLayoutCount - 1 && mNoMoreData) {
                                fab.hide()
                            }else {
                                if (dy > 0) {  //向上滑动
                                    if (firstCompletelyVisibleIndex > 0) {
                                        if (mIsFabUpward) {
                                            mIsFabUpward = false
                                            fab.animate().rotation(180f).start()  //箭头向下动画
                                        }
                                    }
                                } else if (dy < 0) { //向下滑动
                                    if (lastCompletelyVisibleIndex < mAdapter.itemCount) {
                                        if (!mIsFabUpward) {
                                            mIsFabUpward = true
                                            fab.animate().rotation(0f).start()  //箭头向上动画
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
            }else {
                fab.hide()
            }

            fab.setOnClickListener {
                if (mIsFabUpward) {
                    recyclerView.smoothScrollToPosition(0)
                } else {
                    recyclerView.smoothScrollToPosition(mAdapter.data.size)
                }
            }
        }

        val commonRefreshHeaderLayout = CommonRefreshHeaderLayout(context)
        commonRefreshHeaderLayout.setPrimaryColors(ContextCompat.getColor(context, R.color.colorPrimary))
        smartRefreshLayout.setRefreshHeader(commonRefreshHeaderLayout)
        smartRefreshLayout.setRefreshFooter(CommonRefreshFooterLayout(context))

        smartRefreshLayout.setOnRefreshListener {
            mCurrentPage = 0
            mIsRefreshing = true

            commonListPagerListener?.refresh()
        }

        smartRefreshLayout.setOnLoadMoreListener {
            ++mCurrentPage
            mIsLoadingMore = true

            commonListPagerListener?.loadMore(mCurrentPage)
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
            fab.hide()
        }

        if (mCurrentPage == 0) {
            if (mAdapter.data is MutableList<*>) {
                mAdapter.data.clear()
            }
        }

        mAdapter.addData(newData)

        newData.forEach {
            logDebug("new data item=" + it.toString())
        }

        if(mAdapter.data.size == 0) {
            switchEmptyLayout()
        }
    }

    fun removeItem(position: Int) {
        mAdapter.data.removeAt(position)
        mAdapter.notifyItemRemoved(position)
        if(mAdapter.data.size == 0) {
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

    fun hideFab() {
        mShowFab = false
        fab.hide()
        val lp = fab.layoutParams
        lp.width = 0
        lp.height = 0
        fab.requestLayout()
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