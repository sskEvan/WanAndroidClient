package com.ssk.wanandroid.fragment

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.wanandroid.WanWebActivity
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
    private val mBannerTitles = mutableListOf<String>()
    private val mBannerUrls = mutableListOf<String>()
    private var mIsRefreshing = false
    private var mIsLoadingMore = false
    private val mArticleAdapter by lazy { ArticleAdapter() }
    private var mCurrentPage = 0
    private lateinit var rvLayoutManager: LinearLayoutManager
    private var mIsFabUpward = true;

    private var bannerLayout: com.youth.banner.Banner? = null

    override fun getLayoutResId() = R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(false)
        toolbar?.title = "首页"
        immersiveStatusBar(R.color.colorPrimary, true)

        fab.setOnClickListener {
            if(mIsFabUpward) {
                rvArticle.smoothScrollToPosition(0)
            }else {
                rvArticle.smoothScrollToPosition(mArticleAdapter.data.size)
            }
        }

        initRvArticle()
    }

    private fun initRvArticle() {
        rvLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val headerLayout = layoutInflater.inflate(R.layout.layout_article_list_header,
            rvArticle.parent as ViewGroup,
            false)
        bannerLayout = headerLayout.findViewById(R.id.bannerLayout) as com.youth.banner.Banner
        bannerLayout?.run {
            setBannerStyle(BannerConfig.NUM_INDICATOR)
            setImageLoader(GlideImageLoader())
            setOnBannerListener {
                forwardWanWebActivity(mBannerTitles[it], mBannerUrls[it])
            }
        }

        rvArticle.run {
            layoutManager = rvLayoutManager
            adapter = mArticleAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                @SuppressLint("RestrictedApi")
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    var firstCompletelyVisibleIndex = rvLayoutManager.findFirstCompletelyVisibleItemPosition()
                    if(firstCompletelyVisibleIndex == 0 && fab.isShown) {
                        fab.hide()
                    }else {
                        if(!fab.isShown) {
                            fab.show()
                        }
                        var lastCompletelyVisibleIndex = rvLayoutManager.findLastCompletelyVisibleItemPosition()
                        if(dy > 0) {  //向上滑动
                            if(firstCompletelyVisibleIndex > 0) {
                                if(mIsFabUpward) {
                                    mIsFabUpward = false
                                    fab.animate().rotation(180f).start()  //箭头向下动画
                                }
                            }
                        }else if(dy < 0) { //向下滑动
                            if(lastCompletelyVisibleIndex < adapter!!.itemCount) {
                                if(!mIsFabUpward) {
                                    mIsFabUpward = true
                                    fab.animate().rotation(0f).start()  //箭头向上动画
                                }
                            }
                        }
                    }
                }
            })
        }

        mArticleAdapter.run {
            addHeaderView(headerLayout)
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener{_, view, position ->
                when (view.id) {
                    R.id.cvItemRoot -> {
                        forwardWanWebActivity(mArticleAdapter.data[position].title, mArticleAdapter.data[position].link)
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

        switchableConstraintLayout.setRetryListener {
            mViewModel.fetchBanners()
            mViewModel.fetchArticleList(mCurrentPage)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mViewModel.fetchBanners()
        mViewModel.fetchArticleList(mCurrentPage)
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
                }else if(mIsLoadingMore) {
                    srfArticle.finishLoadMore(true)
                    mIsLoadingMore = false
                }else {
                    switchableConstraintLayout.switchSuccessLayout()
                    fab.hide()
                }
                if(it != null) {
                    setArticles(it)
                }
            })

            mArticleListErrorMsg.observe(this@HomeFragment, Observer {
                if(!mIsRefreshing && !mIsLoadingMore) {
                    switchableConstraintLayout.switchFailedLayout()
                }

                if(mIsRefreshing) {
                    srfArticle.finishRefresh(false)
                    mIsRefreshing = false
                }
                if(mIsLoadingMore) {
                    srfArticle.finishLoadMore(false)
                    mIsLoadingMore = false
                }
            })
        }
    }

    private fun setBanner(bannerList: List<Banner>) {
        mBannerImages.clear()
        mBannerUrls.clear()
        for (banner in bannerList) {
            mBannerImages.add(banner.imagePath)
            mBannerUrls.add(banner.url)
            mBannerTitles.add(banner.title)
        }
        bannerLayout!!.setImages(mBannerImages).setDelayTime(3000).start()
    }

    private fun setArticles(articleList: ArticleList) {
        mArticleAdapter.run {
            if(mCurrentPage == 0) {
                data.clear()
            }
            addData(articleList.datas)
        }
    }

    private fun forwardWanWebActivity(title: String, url: String) {
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("url", url)
        startActivity(WanWebActivity::class.java, bundle)
        mActivity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_none)
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