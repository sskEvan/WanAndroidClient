package com.ssk.wanandroid.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.wanandroid.R
import com.ssk.wanandroid.app.WanAndroid
import com.ssk.wanandroid.view.activity.WanWebActivity
import com.ssk.wanandroid.base.WanFragment
import com.ssk.wanandroid.bean.ArticleVo
import com.ssk.wanandroid.event.OnCollectChangedEvent
import com.ssk.wanandroid.event.OnProjectFragmentFabClickResponseEvent
import com.ssk.wanandroid.event.OnProjectFragmentFabUpwardControlEvent
import com.ssk.wanandroid.event.OnProjectFragmentFabVisiableControlEvent
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.view.adapter.ProjectAdapter
import com.ssk.wanandroid.util.EventManager
import com.ssk.wanandroid.view.activity.LoginActivity
import com.ssk.wanandroid.viewmodel.ProjectDetailViewModel
import com.ssk.wanandroid.widget.CollectButton
import com.ssk.wanandroid.widget.CommonListPager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by shisenkun on 2019-07-03.
 */
class ProjectDetailFragment : WanFragment<ProjectDetailViewModel>() {

    companion object {
        fun create(projectId: Int): ProjectDetailFragment = ProjectDetailFragment().apply {
            val bundle = Bundle()
            bundle.putInt("projectId", projectId)
            arguments = bundle
        }
    }

    private val mProjectAdapter by lazy { ProjectAdapter() }
    private var mProjectId = 0
    private var mIsFabShown = false
    private var mIsFabUpward = true
    private var mPosition = 0
    private var mNoMoreData = false
    private lateinit var commonListPager: CommonListPager<ArticleVo>

    override fun getLayoutResId() = R.layout.fragment_project_detail

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        setupCommonListPager()
    }

    private fun setupCommonListPager() {
        commonListPager = mContentView.findViewById(R.id.commonListPager)
        //commonListPager.hideFab()
        commonListPager.setAdapter(mProjectAdapter)
        commonListPager.commonListPagerListener = object : CommonListPager.CommonListPagerListener {
            override fun retry() {
                mViewModel.fetchProjectDetailList(0, mProjectId)
            }

            override fun refresh() {
                mViewModel.fetchProjectDetailList(0, mProjectId)
            }

            override fun loadMore(page: Int) {
                mViewModel.fetchProjectDetailList(page, mProjectId)
            }
        }

        commonListPager.getRecyclerView().run {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                @SuppressLint("RestrictedApi")
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val firstCompletelyVisibleIndex =
                        commonListPager.getLayoutManager().findFirstCompletelyVisibleItemPosition()
                    if (firstCompletelyVisibleIndex == 0) {
                        if (mIsFabShown) {
                            mIsFabShown = false
                            EventManager.post(OnProjectFragmentFabVisiableControlEvent(false))
                        }
                    } else {
                        if (!mIsFabShown) {
                            mIsFabShown = true
                            EventManager.post(OnProjectFragmentFabVisiableControlEvent(true))
                        }

                        val lastCompletelyVisibleIndex =
                            commonListPager.getLayoutManager().findLastCompletelyVisibleItemPosition()
                        if(lastCompletelyVisibleIndex == mProjectAdapter.data.size - 1 && mNoMoreData) {
                            EventManager.post(OnProjectFragmentFabVisiableControlEvent(false))
                        }else {
                            if (dy > 0) {  //向上滑动
                                if (firstCompletelyVisibleIndex > 0) {
                                    if (mIsFabUpward) {
                                        mIsFabUpward = false
                                        EventManager.post(OnProjectFragmentFabUpwardControlEvent(false))
                                    }
                                }
                            } else if (dy < 0) { //向下滑动
                                if (lastCompletelyVisibleIndex < adapter!!.itemCount) {
                                    if (!mIsFabUpward) {
                                        mIsFabUpward = true
                                        EventManager.post(OnProjectFragmentFabUpwardControlEvent(true))
                                    }
                                }
                            }
                        }
                    }
                }
            })
        }

        mProjectAdapter.run {
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                mPosition = position
                when (view.id) {
                    R.id.cvItemRoot -> {
                        forwardWanWebActivity(
                            mProjectAdapter.data[position].title, mProjectAdapter.data[position].link,
                            mProjectAdapter.data[position].id, mProjectAdapter.data[position].collect
                        )
                    }
                    R.id.collectButton -> {
                        if (WanAndroid.currentUser != null) {
                            if (mProjectAdapter.data[position].collect) {
                                (view as CollectButton).startUncollectAnim()
                                mViewModel.unCollectArticle(mProjectAdapter.data[position].id)
                            } else {
                                (view as CollectButton).startCollectAnim()
                                mViewModel.collectArticle(mProjectAdapter.data[position].id)
                            }
                            mProjectAdapter.data[position].collect = !mProjectAdapter.data[position].collect
                        } else {
                            showToast("请先登陆!")
                            startActivity(LoginActivity::class.java, false)
                            mActivity.overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_none)
                        }
                    }
                }
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mProjectId = arguments!!.getInt("projectId")
        mViewModel.fetchProjectDetailList(0, mProjectId)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mProjectDetailList.observe(this@ProjectDetailFragment, Observer {
                commonListPager.addData(it.datas)
                commonListPager.setNoMoreData(it.over)
                mNoMoreData = it.over
            })

            mFetchProjectDetailListErrorMsg.observe(this@ProjectDetailFragment, Observer {
                commonListPager.fetchDataError(it)
            })

            mCollectArticleSuccess.observe(this@ProjectDetailFragment, Observer {
                showSnackBar("收藏成功!")
            })

            mUnCollectArticleSuccess.observe(this@ProjectDetailFragment, Observer {
                showSnackBar("取消收藏成功!")
            })

            mCollectArticleErrorMsg.observe(this@ProjectDetailFragment, Observer {
                showSnackBar(it)
                mProjectAdapter.data[mPosition].collect = !mProjectAdapter.data[mPosition].collect
                mProjectAdapter.notifyItemChanged(mPosition + 1)
            })

            mUnCollectArticleErrorMsg.observe(this@ProjectDetailFragment, Observer {
                showSnackBar(it)
                mProjectAdapter.data[mPosition].collect = !mProjectAdapter.data[mPosition].collect
                mProjectAdapter.notifyItemChanged(mPosition + 1)
            })
        }
    }

    private fun forwardWanWebActivity(title: String, url: String, id: Int, isCollected: Boolean) {
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("url", url)
        bundle.putInt("id", id)
        bundle.putBoolean("isCollected", isCollected)
        startActivity(WanWebActivity::class.java, bundle, true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OnProjectFragmentFabClickResponseEvent) {
        if (event.isUpward) {
            commonListPager.getRecyclerView().smoothScrollToPosition(0)
        } else {
            commonListPager.getRecyclerView().smoothScrollToPosition(mProjectAdapter.data.size)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OnCollectChangedEvent) {
        mProjectAdapter.data.forEach {
            if(event.id == it.id) {
                it.collect = event.isCollected
                mProjectAdapter.notifyItemChanged(mProjectAdapter.data.indexOf(it))
                return
            }
        }
    }

}