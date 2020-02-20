package com.ssk.wanandroid.view.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.lib_annotation.annotation.BindContentView
import com.ssk.wanandroid.R
import com.ssk.wanandroid.app.WanAndroid
import com.ssk.wanandroid.aspect.annotation.CheckLogin
import com.ssk.wanandroid.base.WanActivity
import com.ssk.wanandroid.bean.ArticleVo
import com.ssk.wanandroid.event.OnCollectChangedEvent
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.view.adapter.ArticleAdapter
import com.ssk.wanandroid.viewmodel.KnowledgeDetailViewModel
import com.ssk.wanandroid.widget.CollectButton
import com.ssk.wanandroid.widget.CommonListPager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by shisenkun on 2019-07-08.
 */
@BindContentView(R.layout.activity_knowledge_detail)
class KnowledgeDetailActivity : WanActivity<KnowledgeDetailViewModel>() {

    private val mAdapter by lazy { ArticleAdapter() }
    private lateinit var commonListPager: CommonListPager<ArticleVo>
    private var mPosition = 0

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(true)
        immersiveStatusBar(R.color.colorPrimary, true)

        setupCommonListPager()
    }

    fun setupCommonListPager() {
        commonListPager = findViewById(R.id.commonListPager)
        commonListPager.setAdapter(mAdapter)

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

        mAdapter.run {
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                mPosition = position
                when (view.id) {
                    R.id.cvItemRoot -> {
                        forwardWanWebActivity(
                            mAdapter.data[position].title, mAdapter.data[position].link,
                            mAdapter.data[position].id, mAdapter.data[position].collect
                        )
                    }
                    R.id.collectButton -> {
                        collect(view, position)
                    }
                }
            }
        }
    }

    @CheckLogin
    private fun collect(view: View, position: Int) {
        if (mAdapter.data[position].collect) {
            (view as CollectButton).startUncollectAnim()
            mViewModel.unCollectArticle(mAdapter.data[position].id)
        } else {
            (view as CollectButton).startCollectAnim()
            mViewModel.collectArticle(mAdapter.data[position].id)
        }
        mAdapter.data[position].collect = !mAdapter.data[position].collect
    }

    private fun forwardWanWebActivity(title: String, url: String, id: Int, isCollected: Boolean) {
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("url", url)
        bundle.putInt("id", id)
        bundle.putBoolean("isCollected", isCollected)
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
                mAdapter.data[mPosition].collect = !mAdapter.data[mPosition].collect
                mAdapter.notifyItemChanged(mPosition + 1)
            })

            mUnCollectArticleErrorMsg.observe(this@KnowledgeDetailActivity, Observer {
                showSnackBar(it)
                mAdapter.data[mPosition].collect = !mAdapter.data[mPosition].collect
                mAdapter.notifyItemChanged(mPosition + 1)
            })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OnCollectChangedEvent) {
        if (event.id == mAdapter.data[mPosition].id) {
            mAdapter.data[mPosition].collect = event.isCollected
            mAdapter.notifyItemChanged(mPosition)
        }
    }

}