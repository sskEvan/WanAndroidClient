package com.ssk.wanandroid.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.wanandroid.R
import com.ssk.wanandroid.WanWebActivity
import com.ssk.wanandroid.base.WanFragment
import com.ssk.wanandroid.bean.ArticleListVo
import com.ssk.wanandroid.event.OnProjectFragmentFabClickResponseEvent
import com.ssk.wanandroid.event.OnProjectFragmentFabUpwardControlEvent
import com.ssk.wanandroid.event.OnProjectFragmentFabVisiableControlEvent
import com.ssk.wanandroid.adapter.ProjectAdapter
import com.ssk.wanandroid.service.EventManager
import com.ssk.wanandroid.viewmodel.ProjectDetailViewModel
import com.ssk.wanandroid.widget.CommonRefreshFooterLayout
import com.ssk.wanandroid.widget.CommonRefreshHeaderLayout
import kotlinx.android.synthetic.main.fragment_project_detail.*
import kotlinx.android.synthetic.main.fragment_project_detail.switchableConstraintLayout
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by shisenkun on 2019-07-03.
 */
class ProjectDetailFragment : WanFragment<ProjectDetailViewModel>() {

    companion object {
        fun create(projectId: Int): ProjectDetailFragment {
            val fragment = ProjectDetailFragment()
            val bundle = Bundle()
            bundle.putInt("projectId", projectId)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var mIsRefreshing = false
    private var mIsLoadingMore = false
    private val mProjectAdapter by lazy { ProjectAdapter() }
    private var mCurrentPage = 0
    private lateinit var rvLayoutManager: LinearLayoutManager
    private var mProjectId = 0
    private var mIsFabShown= false
    private var mIsFabUpward = true

    override fun getLayoutResId() = R.layout.fragment_project_detail

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        initRvArticle()
    }

    private fun initRvArticle() {
        rvLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        rvProject.run {
            layoutManager = rvLayoutManager
            adapter = mProjectAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                @SuppressLint("RestrictedApi")
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val firstCompletelyVisibleIndex = rvLayoutManager.findFirstCompletelyVisibleItemPosition()
                    if (firstCompletelyVisibleIndex == 0) {
                        if(mIsFabShown) {
                            mIsFabShown = false
                            EventManager.post(OnProjectFragmentFabVisiableControlEvent(false))
                        }
                    } else {
                        if(!mIsFabShown) {
                            mIsFabShown = true
                            EventManager.post(OnProjectFragmentFabVisiableControlEvent(true))
                        }

                        val lastCompletelyVisibleIndex = rvLayoutManager.findLastCompletelyVisibleItemPosition()
                        if (dy > 0) {  //向上滑动
                            if (firstCompletelyVisibleIndex > 0) {
                                if(mIsFabUpward) {
                                    mIsFabUpward = false
                                    EventManager.post(OnProjectFragmentFabUpwardControlEvent(false))
                                }
                            }
                        } else if (dy < 0) { //向下滑动
                            if (lastCompletelyVisibleIndex < adapter!!.itemCount) {
                                if(!mIsFabUpward) {
                                    mIsFabUpward = true
                                    EventManager.post(OnProjectFragmentFabUpwardControlEvent(true))
                                }
                            }
                        }
                    }
                }
            })
        }

        mProjectAdapter.run {
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.cvItemRoot -> {
                        forwardWanWebActivity(mProjectAdapter.data[position].title, mProjectAdapter.data[position].link)
                    }
                }
            }
        }

        val commonRefreshHeaderLayout = CommonRefreshHeaderLayout(mActivity)
        commonRefreshHeaderLayout.setPrimaryColors(ContextCompat.getColor(mActivity, R.color.colorPrimary))
        srfProject.setRefreshHeader(commonRefreshHeaderLayout)
        srfProject.setRefreshFooter(CommonRefreshFooterLayout(mActivity))
        srfProject.setOnRefreshListener {
            mCurrentPage = 0
            mIsRefreshing = true
            mViewModel.fetchProjectDetailList(mCurrentPage, mProjectId)
        }
        srfProject.setOnLoadMoreListener {
            ++mCurrentPage
            mIsLoadingMore = true
            mViewModel.fetchProjectDetailList(mCurrentPage, mProjectId)
        }

        switchableConstraintLayout.setRetryListener {
            mViewModel.fetchProjectDetailList(mCurrentPage, mProjectId)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mProjectId = arguments!!.getInt("projectId")
        mViewModel.fetchProjectDetailList(mCurrentPage, mProjectId)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mProjectDetailList.observe(this@ProjectDetailFragment, Observer {
                if (mIsRefreshing) {
                    srfProject.finishRefresh(true)
                    mIsRefreshing = false
                } else if (mIsLoadingMore) {
                    srfProject.finishLoadMore(true)
                    mIsLoadingMore = false
                } else {
                    switchableConstraintLayout.switchSuccessLayout()
                    EventManager.post(OnProjectFragmentFabVisiableControlEvent(false))
                }
                if (it != null) {
                    setProjectList(it)
                }
            })

            mFetchProjectDetailListErrorMsg.observe(this@ProjectDetailFragment, Observer {
                if (!mIsRefreshing && !mIsLoadingMore) {
                    switchableConstraintLayout.switchFailedLayout(it)
                }

                if (mIsRefreshing) {
                    srfProject.finishRefresh(false)
                    mIsRefreshing = false
                }
                if (mIsLoadingMore) {
                    srfProject.finishLoadMore(false)
                    mIsLoadingMore = false
                }
            })
        }
    }

    private fun setProjectList(projectListVo: ArticleListVo) {
        if(projectListVo.datas == null || projectListVo.datas.size == 0) {
            switchableConstraintLayout.switchEmptyLayout()
        }else {
            mProjectAdapter.run {
                if (mCurrentPage == 0) {
                    data.clear()
                }
                addData(projectListVo.datas)

                srfProject.setNoMoreData(projectListVo.over)
            }
        }
    }

    private fun forwardWanWebActivity(title: String, url: String) {
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("url", url)
        startActivity(WanWebActivity::class.java, bundle)
        mActivity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_none)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OnProjectFragmentFabClickResponseEvent) {
        if(event.isUpward) {
            rvProject.smoothScrollToPosition(0)
        }else {
            rvProject.smoothScrollToPosition(mProjectAdapter.data.size)
        }
    }

}