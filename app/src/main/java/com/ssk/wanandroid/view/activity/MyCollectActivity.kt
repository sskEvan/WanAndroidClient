package com.ssk.wanandroid.view.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.WanActivity
import com.ssk.wanandroid.bean.ArticleVo
import com.ssk.wanandroid.event.OnCollectChangedEvent
import com.ssk.wanandroid.ext.logDebug
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.util.EventManager
import com.ssk.wanandroid.view.adapter.ArticleAdapter
import com.ssk.wanandroid.viewmodel.MyCollectViewModel
import com.ssk.wanandroid.widget.CollectButton
import com.ssk.wanandroid.widget.CommonListPager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by shisenkun on 2019-07-22.
 */
class MyCollectActivity : WanActivity<MyCollectViewModel>() {

    private lateinit var commonListPager: CommonListPager<ArticleVo>
    private val mArticleAdapter by lazy { ArticleAdapter() }
    private var mPosition = 0

    override fun getLayoutId() = R.layout.activity_my_collect

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(true)
        immersiveStatusBar(R.color.colorPrimary, true)
        setupCommonListPager()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mViewModel.fetchCollectArticleList(0)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {

            mFetchCollectArticleResult.observe(this@MyCollectActivity, Observer { it ->
                commonListPager.addData(it.datas)
                commonListPager.setNoMoreData(it.over)
            })

            mFetchCollectArticleErrorMsg.observe(this@MyCollectActivity, Observer {
                commonListPager.fetchDataError(it)
            })

            mUnCollectArticleSuccess.observe(this@MyCollectActivity, Observer {
                showSnackBar("取消收藏成功!")
                EventManager.post(OnCollectChangedEvent(false, mArticleAdapter.data[mPosition].originId))
            })

            mUnCollectArticleErrorMsg.observe(this@MyCollectActivity, Observer {
                showSnackBar(it)
                mArticleAdapter.data[mPosition].collect = !mArticleAdapter.data[mPosition].collect
                mArticleAdapter.notifyItemChanged(mPosition)
            })
        }
    }

    private fun setupCommonListPager() {
        commonListPager = findViewById(R.id.commonListPager)

        commonListPager.setAdapter(mArticleAdapter)
        commonListPager.commonListPagerListener = object : CommonListPager.CommonListPagerListener {
            override fun retry() {
                mViewModel.fetchCollectArticleList(0)
            }

            override fun refresh() {
                mViewModel.fetchCollectArticleList(0)
            }

            override fun loadMore(page: Int) {
                mViewModel.fetchCollectArticleList(page)
            }
        }

        mArticleAdapter.run {
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener{ _, view, position ->
                mPosition = position
                when (view.id) {
                    R.id.cvItemRoot -> {
                        forwardWanWebActivity(mArticleAdapter.data[position].title, mArticleAdapter.data[position].link,
                            mArticleAdapter.data[position].originId, mArticleAdapter.data[position].collect)
                    }
                    R.id.collectButton -> {
                        (view as CollectButton).startUncollectAnim()
                        mViewModel.unCollectArticle(mArticleAdapter.data[position].originId)
                        mArticleAdapter.data[position].collect = !mArticleAdapter.data[position].collect
                    }
                }
            }
        }
    }

    private fun forwardWanWebActivity(title: String, url: String, id: Int, isCollected: Boolean) {
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("url", url)
        bundle.putInt("id", id)
        bundle.putBoolean("isCollected", isCollected)
        startActivity(WanWebActivity::class.java, bundle)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OnCollectChangedEvent) {
        if(event.id == mArticleAdapter.data[mPosition].originId && !event.isCollected) {
            commonListPager.removeItem(mPosition)
        }
    }

}