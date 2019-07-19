package com.ssk.wanandroid

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.wanandroid.adapter.KnowledgeAdapter
import com.ssk.wanandroid.base.WanActivity
import com.ssk.wanandroid.bean.ArticleListVo
import com.ssk.wanandroid.viewmodel.KnowledgeDetailViewModel
import com.ssk.wanandroid.widget.CommonRefreshFooterLayout
import com.ssk.wanandroid.widget.CommonRefreshHeaderLayout
import kotlinx.android.synthetic.main.activity_knowledge_detail.*
import kotlinx.android.synthetic.main.activity_knowledge_detail.switchableConstraintLayout

/**
 * Created by shisenkun on 2019-07-08.
 */
class KnowledgeDetailActivity : WanActivity<KnowledgeDetailViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_knowledge_detail
    private var mCurrentPage = 0
    private var mIsRefreshing = false
    private var mIsLoadingMore = false
    private lateinit var rvLayoutManager: LinearLayoutManager
    private val mKnowledgeAdapter by lazy { KnowledgeAdapter() }


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(true)
        immersiveStatusBar(R.color.colorPrimary, true)

        initRvKnowledge()
    }

    fun initRvKnowledge() {
        rvLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        rvKnowledge.run {
            layoutManager = rvLayoutManager
            adapter = mKnowledgeAdapter
        }

        val commonRefreshHeaderLayout = CommonRefreshHeaderLayout(this)
        commonRefreshHeaderLayout.setPrimaryColors(ContextCompat.getColor(this, R.color.colorPrimary))
        srfKnowledge.setRefreshHeader(commonRefreshHeaderLayout)
        srfKnowledge.setRefreshFooter(CommonRefreshFooterLayout(this))

        srfKnowledge.setOnRefreshListener {
            mCurrentPage = 0
            mIsRefreshing = true
            mViewModel.fetchKnowledgeDetailList(mCurrentPage, intent.extras.getInt("knowledgeId"))
        }
        srfKnowledge.setOnLoadMoreListener {
            ++mCurrentPage
            mIsLoadingMore = true
            mViewModel.fetchKnowledgeDetailList(mCurrentPage, intent.extras.getInt("knowledgeId"))
        }

        switchableConstraintLayout.setRetryListener {
            mViewModel.fetchKnowledgeDetailList(mCurrentPage, intent.extras.getInt("knowledgeId"))
        }

        mKnowledgeAdapter.run {
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.cvItemRoot -> {
                        forwardWanWebActivity(mKnowledgeAdapter.data[position].title, mKnowledgeAdapter.data[position].link)
                    }
                }
            }
        }
    }

    private fun forwardWanWebActivity(title: String, url: String) {
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("url", url)
        startActivity(WanWebActivity::class.java, bundle)
    }


    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mViewModel.fetchKnowledgeDetailList(mCurrentPage, intent.extras.getInt("knowledgeId"))
    }

    override fun onResume() {
        super.onResume()
        toolbar?.title = intent.extras?.getString("title")
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mKnowledgeDetailList.observe(this@KnowledgeDetailActivity, Observer {
                if (mIsRefreshing) {
                    srfKnowledge.finishRefresh(true)
                    mIsRefreshing = false
                } else if (mIsLoadingMore) {
                    srfKnowledge.finishLoadMore(true)
                    mIsLoadingMore = false
                } else {
                    switchableConstraintLayout.switchSuccessLayout()
                }
                if (it != null) {
                    setKnowledgeList(it)
                }
            })

            mFetchKnowledgeDetailListErrorMsg.observe(this@KnowledgeDetailActivity, Observer {
                if (!mIsRefreshing && !mIsLoadingMore) {
                    switchableConstraintLayout.switchFailedLayout(it)
                }

                if (mIsRefreshing) {
                    srfKnowledge.finishRefresh(false)
                    mIsRefreshing = false
                }
                if (mIsLoadingMore) {
                    srfKnowledge.finishLoadMore(false)
                    mIsLoadingMore = false
                }
            })
        }
    }

    private fun setKnowledgeList(knowledgeListVo: ArticleListVo) {
        if(knowledgeListVo.datas == null || knowledgeListVo.datas.size == 0) {
            switchableConstraintLayout.switchEmptyLayout()
        }else {
            mKnowledgeAdapter.run {
                if (mCurrentPage == 0) {
                    data.clear()
                }
                addData(knowledgeListVo.datas)

                srfKnowledge.setNoMoreData(knowledgeListVo.over)
            }
        }
    }

}