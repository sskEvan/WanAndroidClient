package com.ssk.wanandroid

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.text.TextUtils
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.wanandroid.adapter.ArticleAdapter
import com.ssk.wanandroid.base.WanActivity
import com.ssk.wanandroid.bean.ArticleListVo
import com.ssk.wanandroid.bean.HotSearchVo
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.utils.AndroidVersion
import com.ssk.wanandroid.utils.TransitionUtils
import com.ssk.wanandroid.viewmodel.SearchViewModel
import com.ssk.wanandroid.widget.CommonRefreshFooterLayout
import com.ssk.wanandroid.widget.CommonRefreshHeaderLayout
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.rvArticle
import kotlinx.android.synthetic.main.activity_search.srfArticle

/**
 * Created by shisenkun on 2019-06-22.
 */
class SearchActivity : WanActivity<SearchViewModel>() {

    private var mCurrentPage = 0
    private var mIsRefreshing = false
    private var mIsLoadingMore = false
    private val mArticleAdapter by lazy { ArticleAdapter() }
    private lateinit var rvLayoutManager: LinearLayoutManager
    private var mSearchKey: String? = null
    private lateinit var etSearch: EditText

    override fun getLayoutId() = R.layout.activity_search

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        immersiveStatusBar(R.color.colorPrimary, true)
        setupSearchView()
        setupRvArticle()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        mViewModel.fetchHotSearch()
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mHotSearchVoList.observe(this@SearchActivity, Observer {
                setupHotSearchTagLayout(it)
            })

            mSearchResult.observe(this@SearchActivity, Observer {
                if (mIsRefreshing) {
                    srfArticle.finishRefresh(true)
                    mIsRefreshing = false
                } else if (mIsLoadingMore) {
                    srfArticle.finishLoadMore(true)
                    mIsLoadingMore = false
                } else {
                    sclSearch.switchSuccessLayout()
                }
                if (it != null) {
                    setArticles(it)
                }
            })

            mSearchResultErrorMsg.observe(this@SearchActivity, Observer {
                if (!mIsRefreshing && !mIsLoadingMore) {
                    sclSearch.switchFailedLayout(it)
                }

                if (mIsRefreshing) {
                    srfArticle.finishRefresh(false)
                    mIsRefreshing = false
                }
                if (mIsLoadingMore) {
                    srfArticle.finishLoadMore(false)
                    mIsLoadingMore = false
                }
            })
        }
    }

    private fun setArticles(articleListVo: ArticleListVo) {
        mArticleAdapter.run {
            if (mCurrentPage == 0) {
                data.clear()
            }
            addData(articleListVo.datas)
            srfArticle.setNoMoreData(articleListVo.over)
        }
    }


    private fun setupSearchView() {
        searchView.queryHint = "请输入关键字搜索文章"
        searchView.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        searchView.imeOptions = searchView.imeOptions or EditorInfo.IME_ACTION_SEARCH or
                EditorInfo.IME_FLAG_NO_EXTRACT_UI or EditorInfo.IME_FLAG_NO_FULLSCREEN
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                hideKeyboard()
                mSearchKey = query
                tvHotSearchLabel.visibility = View.INVISIBLE
                hotSearchTagLayout.visibility = View.INVISIBLE
                sclSearch.visibility = View.VISIBLE
                sclSearch.switchLoadingLayout()
                mCurrentPage = 0
                mViewModel.fetchSearchResult(mCurrentPage, mSearchKey!!)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (TextUtils.isEmpty(query)) {
                    srfArticle.visibility = View.INVISIBLE
                    tvHotSearchLabel.visibility = View.VISIBLE
                    hotSearchTagLayout.visibility = View.VISIBLE
                }
                return true
            }
        })

        window.enterTransition.addListener(object : TransitionUtils.TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                searchView.requestFocus()
                showKeyboard()
                window.enterTransition.removeListener(this)
            }
        })

        ibBack.setOnClickListener {
            finishActivity()
        }
    }

    private fun setupRvArticle() {
        rvLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvArticle.run {
            layoutManager = rvLayoutManager
            adapter = mArticleAdapter
        }

        mArticleAdapter.run {
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.cvItemRoot -> {
                        forwardWanWebActivity(Html.fromHtml(mArticleAdapter.data[position].title).toString(), mArticleAdapter.data[position].link)
                    }
                }
            }
        }

        val commonRefreshHeaderLayout = CommonRefreshHeaderLayout(this)
        commonRefreshHeaderLayout.setPrimaryColors(ContextCompat.getColor(this, R.color.colorPrimary))
        srfArticle.setRefreshHeader(commonRefreshHeaderLayout)
        srfArticle.setRefreshFooter(CommonRefreshFooterLayout(this))
        srfArticle.setOnRefreshListener {
            mCurrentPage = 0
            mIsRefreshing = true
            mViewModel.fetchSearchResult(mCurrentPage, mSearchKey!!)
        }
        srfArticle.setOnLoadMoreListener {
            ++mCurrentPage
            mIsLoadingMore = true
            mViewModel.fetchSearchResult(mCurrentPage, mSearchKey!!)
        }

        sclSearch.setRetryListener {
            mViewModel.fetchSearchResult(mCurrentPage, mSearchKey!!)
        }
    }

    private fun setupHotSearchTagLayout(hotSearchVoList: List<HotSearchVo>) {
        if (!hotSearchVoList.isEmpty()) {
            tvHotSearchLabel.visibility = View.VISIBLE

            hotSearchTagLayout.run {
                adapter = object : TagAdapter<HotSearchVo>(hotSearchVoList) {
                    override fun getCount() = hotSearchVoList.size

                    override fun getView(parent: FlowLayout, position: Int, t: HotSearchVo): View {
                        val tv = LayoutInflater.from(parent.context).inflate(
                            R.layout.item_hot_search_tag,
                            parent,
                            false
                        ) as TextView
                        tv.text = t.name
                        return tv
                    }
                }

                setOnTagClickListener { _, position, _ ->
                    hideKeyboard()
                    mSearchKey = hotSearchVoList[position].name
                    etSearch.setText(mSearchKey)
                    tvHotSearchLabel.visibility = View.INVISIBLE
                    hotSearchTagLayout.visibility = View.INVISIBLE
                    sclSearch.visibility = View.VISIBLE
                    sclSearch.switchLoadingLayout()
                    mCurrentPage = 0
                    mViewModel.fetchSearchResult(mCurrentPage, mSearchKey!!)
                    true
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

    private fun finishActivity() {
        hideKeyboard()

        if (AndroidVersion.hasLollipop()) {
            finishAfterTransition()
        } else {
            finish()
        }
    }

    private fun showKeyboard() {
        try {
            val id = searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
            etSearch = searchView.findViewById<EditText>(id)
            showSoftKeyboard(etSearch)
        } catch (e: Exception) {
        }
    }

    private fun hideKeyboard() {
        searchView.clearFocus()
        hideSoftKeyboard()
    }

}