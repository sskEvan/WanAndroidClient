package com.ssk.wanandroid.view.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.wanandroid.R
import com.ssk.wanandroid.app.WanAndroid
import com.ssk.wanandroid.view.adapter.KnowledgeAdapter
import com.ssk.wanandroid.base.WanActivity
import com.ssk.wanandroid.bean.ArticleVo
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.viewmodel.KnowledgeDetailViewModel
import com.ssk.wanandroid.widget.CollectButton
import com.ssk.wanandroid.widget.CommonListPager

/**
 * Created by shisenkun on 2019-07-08.
 */
class KnowledgeDetailActivity : WanActivity<KnowledgeDetailViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_knowledge_detail
    private val mKnowledgeAdapter by lazy { KnowledgeAdapter() }
    private lateinit var commonListPager: CommonListPager<ArticleVo>
    private var mCollectPosition = 0

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(true)
        immersiveStatusBar(R.color.colorPrimary, true)

        setupCommonListPager()
    }

    fun setupCommonListPager() {
        commonListPager = findViewById(R.id.commonListPager)
        commonListPager.setAdapter(mKnowledgeAdapter)

        commonListPager.commonListPagerListener = object : CommonListPager.CommonListPagerListener {
            override fun retry() {
                mViewModel.fetchKnowledgeDetailList(0, intent.extras!!.getInt("knowledgeId"))
            }

            override fun refresh() {
                mViewModel.fetchKnowledgeDetailList(0, intent.extras!!.getInt("knowledgeId"))
            }

            override fun loadMore(page: Int) {
                mViewModel.fetchKnowledgeDetailList(page, intent.extras!!.getInt("knowledgeId"))
            }
        }

        mKnowledgeAdapter.run {
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.cvItemRoot -> {
                        forwardWanWebActivity(mKnowledgeAdapter.data[position].title, mKnowledgeAdapter.data[position].link)
                    }
                    R.id.collectButton -> {
                        if(WanAndroid.currentUser != null) {
                            mCollectPosition = position
                            if(mKnowledgeAdapter.data[position].collect) {
                                (view as CollectButton).startUncollectAnim()
                                mViewModel.unCollectArticle(mKnowledgeAdapter.data[position].id)
                            }else {
                                (view as CollectButton).startCollectAnim()
                                mViewModel.collectArticle(mKnowledgeAdapter.data[position].id)
                            }
                            mKnowledgeAdapter.data[position].collect = !mKnowledgeAdapter.data[position].collect
                        }else {
                            showToast("请先登陆!")
                            startActivity(LoginActivity::class.java)
                            overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_none)
                        }
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
        mViewModel.fetchKnowledgeDetailList(0, intent.extras!!.getInt("knowledgeId"))
    }

    override fun onResume() {
        super.onResume()
        toolbar?.title = intent.extras?.getString("title")
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mKnowledgeDetailList.observe(this@KnowledgeDetailActivity, Observer {
                commonListPager.addData(it.datas)
                commonListPager.setNoMoreData(it.over)
            })

            mFetchKnowledgeDetailListErrorMsg.observe(this@KnowledgeDetailActivity, Observer {
                commonListPager.fetchDataError(it)
            })

            mCollectArticleSuccess.observe(this@KnowledgeDetailActivity, Observer {
                showSnackBar("收藏成功!")
            })

            mUnCollectArticleSuccess.observe(this@KnowledgeDetailActivity, Observer {
                showSnackBar("取消收藏成功!")
            })

            mCollectArticleErrorMsg.observe(this@KnowledgeDetailActivity, Observer {
                showSnackBar(it)
                mKnowledgeAdapter.data[mCollectPosition].collect = !mKnowledgeAdapter.data[mCollectPosition].collect
                mKnowledgeAdapter.notifyItemChanged(mCollectPosition + 1)
            })

            mUnCollectArticleErrorMsg.observe(this@KnowledgeDetailActivity, Observer {
                showSnackBar(it)
                mKnowledgeAdapter.data[mCollectPosition].collect = !mKnowledgeAdapter.data[mCollectPosition].collect
                mKnowledgeAdapter.notifyItemChanged(mCollectPosition + 1)
            })
        }
    }

}