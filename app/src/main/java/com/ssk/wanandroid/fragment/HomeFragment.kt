package com.ssk.wanandroid.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.wanandroid.ArticleDetailActivity
import com.ssk.wanandroid.R
import com.ssk.wanandroid.SearchActivity
import com.ssk.wanandroid.base.WanFragment
import com.ssk.wanandroid.bean.ArticleList
import com.ssk.wanandroid.bean.Banner
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.fragment.adapter.ArticleAdapter
import com.ssk.wanandroid.utils.AndroidVersion
import com.ssk.wanandroid.utils.GlideImageLoader
import com.ssk.wanandroid.viewmodel.HomeViewModel
import com.ssk.wanandroid.widget.CommonRefreshFooterLayout
import com.ssk.wanandroid.widget.CommonRefreshHeaderLayout
import com.ssk.wanandroid.widget.SwitchableConstraintLayout
import com.youth.banner.BannerConfig
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * Created by shisenkun on 2019-06-23.
 */
class HomeFragment : WanFragment<HomeViewModel>() {

    companion object {
        fun create(): HomeFragment {
            return HomeFragment()
        }
    }

    private val mBannerImages = mutableListOf<String>()
    private val mBannerUrls = mutableListOf<String>()
    private var mIsRefreshing = false
    private var mIsLoadingMore = false
    private val mArticleAdapter by lazy { ArticleAdapter() }
    private var mCurrentPage = 0

    private var bannerLayout: com.youth.banner.Banner? = null

    override fun getLayoutResId() = R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(false)
        toolbar?.title = "首页"
        immersiveStatusBar(R.color.colorPrimary, true)
        initRvArticle()
    }

    private fun initRvArticle() {
        val headerLayout = layoutInflater.inflate(R.layout.layout_article_list_header,
            rvArticle.parent as ViewGroup,
            false)
        bannerLayout = headerLayout.findViewById(R.id.bannerLayout) as com.youth.banner.Banner
        bannerLayout?.run {
            setBannerStyle(BannerConfig.NUM_INDICATOR)
            setImageLoader(GlideImageLoader())
            setOnBannerListener() {
                showToast("click POSITION $it")
            }
        }

        rvArticle.run {
            layoutManager = LinearLayoutManager(mActivity)
            rvArticle.adapter = mArticleAdapter
        }

        mArticleAdapter.run {
            addHeaderView(headerLayout)
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener{_, view, position ->
                when (view.id) {
                    R.id.cvItemRoot -> {
                        val bundle = Bundle()
                        bundle.putString("title", mArticleAdapter.data[position].title)
                        bundle.putString("url", mArticleAdapter.data[position].link)
                        startActivity(ArticleDetailActivity::class.java, bundle)
                    }
                    R.id.ivStar -> {
                        (view as ImageView).setImageResource(R.mipmap.ic_starred)
                    }
                }
            }
        }

        val commonRefreshHeaderLayout = CommonRefreshHeaderLayout(mActivity)
        commonRefreshHeaderLayout.setPrimaryColors(ContextCompat.getColor(mActivity, R.color.colorPrimary))
        srfArticle.setRefreshHeader(commonRefreshHeaderLayout)
        srfArticle.setRefreshFooter(CommonRefreshFooterLayout(mActivity))
        srfArticle.setOnRefreshListener {
            mCurrentPage = 0
            mIsRefreshing = true
            mViewModel.fetchArticleList(mCurrentPage)
        }
        srfArticle.setOnLoadMoreListener {
            ++mCurrentPage;
            mIsLoadingMore = true
            mViewModel.fetchArticleList(mCurrentPage)
        }

        switchableConstraintLayout.mRetryListener = object : SwitchableConstraintLayout.RetryListener {
            override fun retry() {
                mViewModel.fetchBanners()
                mViewModel.fetchArticleList(mCurrentPage)
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mViewModel.fetchBanners()
        mViewModel.fetchArticleList(mCurrentPage)
    }

    override fun startObserve() {
        mViewModel.apply {
            mBanners.observe(this@HomeFragment, Observer { it ->
                it?.let {
                    setBanner(it)
                }
            })

            mArticleList.observe(this@HomeFragment, Observer { it ->
                if(mIsRefreshing) {
                    srfArticle.finishRefresh(true)
                    mIsRefreshing = false
                }else {
                    switchableConstraintLayout.switchSuccessLayout()
                }
                if(mIsLoadingMore) {
                    srfArticle.finishLoadMore(true)
                    mIsLoadingMore = false
                }
                if(it != null) {
                    setArticles(it)
                }
            })

            mFetchArticleListErrorMsg.observe(this@HomeFragment, Observer {
                switchableConstraintLayout.switchFailedLayout()
            })
            mIsFetchArticleListException.observe(this@HomeFragment, Observer {
                if(it) {
                    switchableConstraintLayout.switchFailedLayout()
                }
            })
        }
    }

    private fun setBanner(bannerList: List<Banner>) {
        for (banner in bannerList) {
            mBannerImages.add(banner.imagePath)
            mBannerUrls.add(banner.url)
        }
        bannerLayout!!.setImages(mBannerImages).setDelayTime(3000).start()
    }

    private fun setArticles(articleList: ArticleList) {
        mArticleAdapter.run {
            Log.d("ssk", "setArticles")
            if(mCurrentPage == 0) {
                data.clear()
            }

            addData(articleList.datas)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            immersiveStatusBar(R.color.colorPrimary, true)
            animateToolbarTitle()
            bannerLayout?.startAutoPlay()
        }else {
            bannerLayout?.stopAutoPlay()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_search -> {
                if (AndroidVersion.hasLollipopMR1()) { // Android 5.0版本启用transition动画会存在一些效果上的异常，因此这里只在Android 5.1以上启用此动画
                    val searchMenuView: View? = toolbar?.findViewById(R.id.menu_item_search)
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        mActivity, searchMenuView,
                        getString(R.string.transition_search_back)
                    ).toBundle()
                    startActivity(Intent(mActivity, SearchActivity::class.java), options)
                } else {
                    startActivity(Intent(mActivity, SearchActivity::class.java))
                }
            }
        }
        return true
    }

}