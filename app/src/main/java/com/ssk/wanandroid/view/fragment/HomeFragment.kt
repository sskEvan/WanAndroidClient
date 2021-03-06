package com.ssk.wanandroid.view.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.lib_annotation.annotation.BindContentView
import com.ssk.wanandroid.view.activity.WanWebActivity
import com.ssk.wanandroid.R
import com.ssk.wanandroid.view.activity.SearchActivity
import com.ssk.wanandroid.base.WanFragment
import com.ssk.wanandroid.bean.BannerVo
import com.ssk.wanandroid.view.adapter.ArticleAdapter
import com.ssk.wanandroid.util.AndroidVersion
import com.ssk.wanandroid.util.BannerGlideImageLoader
import com.ssk.wanandroid.viewmodel.HomeViewModel
import com.ssk.wanandroid.widget.CollectButton
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.ssk.wanandroid.aspect.annotation.CheckLogin
import com.ssk.wanandroid.bean.ArticleVo
import com.ssk.wanandroid.event.OnCollectChangedEvent
import com.ssk.wanandroid.widget.CommonListPager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by shisenkun on 2019-06-23.
 */
@BindContentView(R.layout.fragment_home)
class HomeFragment : WanFragment<HomeViewModel>() {

    companion object {
        private const val REQUEST_CODE_COLLECT = 100

        fun create() = HomeFragment()
    }

    private val mBannerImages = mutableListOf<String>()
    private val mBannerTitles = mutableListOf<String>()
    private val mBannerUrls = mutableListOf<String>()

    private val mArticleAdapter by lazy { ArticleAdapter() }
    private var mPosition = 0
    private lateinit var mCollectButton: CollectButton

    private var bannerLayout: Banner? = null
    private lateinit var commonListPager: CommonListPager<ArticleVo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(false)
        toolbar = mContentView.findViewById(R.id.toolbar)
        toolbar?.title = "首页"
        toolbar?.inflateMenu(R.menu.menu_home)
        toolbar?.setOnMenuItemClickListener {
            when (it.itemId) {
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
            true
        }
        immersiveStatusBar(R.color.colorPrimary, true)

        setupCommonListPager()
    }

    private fun setupCommonListPager() {
        commonListPager = mContentView.findViewById(R.id.commonListPager)
        val headerLayout = layoutInflater.inflate(
            R.layout.layout_article_list_header,
            commonListPager.getRecyclerView().parent as ViewGroup,
            false
        )
        bannerLayout = headerLayout.findViewById(R.id.bannerLayout) as Banner
        bannerLayout?.run {
            setBannerStyle(BannerConfig.NUM_INDICATOR)
            setImageLoader(BannerGlideImageLoader())
            setOnBannerListener {
                forwardWanWebActivity(
                    mBannerTitles[it], mBannerUrls[it],
                    mArticleAdapter.data[it].id, mArticleAdapter.data[it].collect
                )
            }
        }

        commonListPager.setAdapter(mArticleAdapter)
        commonListPager.commonListPagerListener = object : CommonListPager.CommonListPagerListener {
            override fun retry() {
                mViewModel.fetchBannerList()
                mViewModel.fetchArticleList(0)
            }

            override fun refresh() {
                mViewModel.fetchArticleList(0)
            }

            override fun loadMore(page: Int) {
                mViewModel.fetchArticleList(page)
            }
        }

        mArticleAdapter.run {
            addHeaderView(headerLayout)
            onItemChildClickListener =
                BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                    mPosition = position
                    mCollectButton = view as CollectButton
                    when (view.id) {
                        R.id.cvItemRoot -> {
                            forwardWanWebActivity(
                                mArticleAdapter.data[position].title,
                                mArticleAdapter.data[position].link,
                                mArticleAdapter.data[position].id,
                                mArticleAdapter.data[position].collect
                            )
                        }
                        R.id.collectButton -> {
                            handleCollectAction()
                        }
                    }
                }
        }
    }

    @CheckLogin(REQUEST_CODE_COLLECT)
    private fun handleCollectAction() {
        if (mArticleAdapter.data[mPosition].collect) {
            mCollectButton.startUncollectAnim()
            mViewModel.unCollectArticle(mArticleAdapter.data[mPosition].id)
        } else {
            mCollectButton.startCollectAnim()
            mViewModel.collectArticle(mArticleAdapter.data[mPosition].id)
        }
        mArticleAdapter.data[mPosition].collect = !mArticleAdapter.data[mPosition].collect
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mViewModel.fetchBannerList()
        mViewModel.fetchArticleList(0)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            immersiveStatusBar(R.color.colorPrimary, true)
            toolbar?.title = "首页"
            animateToolbarTitle()
            bannerLayout?.startAutoPlay()
        } else {
            bannerLayout?.stopAutoPlay()
        }
    }

    override fun startObserve() {
        mViewModel.apply {
            mBannerVoList.observe(this@HomeFragment, Observer { it ->
                it?.let {
                    updateBanner(it)
                }
            })

            mArticalListVo.observe(this@HomeFragment, Observer { it ->
                commonListPager.addData(it.datas)
                commonListPager.setNoMoreData(it.over)
            })

            mFetchArticleListErrorMsg.observe(this@HomeFragment, Observer {
                commonListPager.fetchDataError(it)
            })

            mCollectArticleSuccess.observe(this@HomeFragment, Observer {
                showSnackBar("收藏成功!")
            })

            mUnCollectArticleSuccess.observe(this@HomeFragment, Observer {
                showSnackBar("取消收藏成功!")
            })

            mCollectArticleErrorMsg.observe(this@HomeFragment, Observer {
                showSnackBar(it)
                mArticleAdapter.data[mPosition].collect = !mArticleAdapter.data[mPosition].collect
                mArticleAdapter.notifyItemChanged(mPosition + 1)
            })

            mUnCollectArticleErrorMsg.observe(this@HomeFragment, Observer {
                showSnackBar(it)
                mArticleAdapter.data[mPosition].collect = !mArticleAdapter.data[mPosition].collect
                mArticleAdapter.notifyItemChanged(mPosition + 1)
            })
        }
    }

    private fun updateBanner(bannerList: List<BannerVo>) {
        mBannerImages.clear()
        mBannerUrls.clear()
        for (banner in bannerList) {
            mBannerImages.add(banner.imagePath)
            mBannerUrls.add(banner.url)
            mBannerTitles.add(banner.title)
        }
        bannerLayout!!.setImages(mBannerImages).setDelayTime(3000).start()
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
    fun onEvent(event: OnCollectChangedEvent) {
        mArticleAdapter.data.forEach {
            if (event.id == it.id) {
                it.collect = event.isCollected
                mArticleAdapter.notifyItemChanged(mArticleAdapter.data.indexOf(it) + 1)
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_COLLECT) {
            handleCollectAction()
        }
    }

}
