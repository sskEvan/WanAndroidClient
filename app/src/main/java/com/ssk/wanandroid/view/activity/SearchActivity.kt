package com.ssk.wanandroid.view.activity

import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.wanandroid.R
import com.ssk.wanandroid.app.WanAndroid
import com.ssk.wanandroid.view.adapter.ArticleAdapter
import com.ssk.wanandroid.base.WanActivity
import com.ssk.wanandroid.bean.ArticleVo
import com.ssk.wanandroid.bean.HotSearchVo
import com.ssk.wanandroid.event.OnCollectChangedEvent
import com.ssk.wanandroid.ext.fromHtml
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.util.AndroidVersion
import com.ssk.wanandroid.util.EventManager
import com.ssk.wanandroid.util.TransitionUtil
import com.ssk.wanandroid.viewmodel.SearchViewModel
import com.ssk.wanandroid.widget.CollectButton
import com.ssk.wanandroid.widget.CommonListPager
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_search.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by shisenkun on 2019-06-22.
 */
class SearchActivity : WanActivity<SearchViewModel>() {

    private val mArticleAdapter by lazy { ArticleAdapter() }
    private var mSearchKey: String? = null
    private lateinit var etSearch: EditText
    private lateinit var commonListPager: CommonListPager<ArticleVo>
    private var mPosition = 0

    override fun getLayoutId() = R.layout.activity_search

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        immersiveStatusBar(R.color.colorPrimary, true)
        setupSearchView()
        setupCommonListPager()
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
                commonListPager.addData(it.datas)
                commonListPager.setNoMoreData(it.over)
            })

            mSearchResultErrorMsg.observe(this@SearchActivity, Observer {
                commonListPager.fetchDataError(it)
            })

            mCollectArticleSuccess.observe(this@SearchActivity, Observer {
                showSnackBar("收藏成功!")
            })

            mUnCollectArticleSuccess.observe(this@SearchActivity, Observer {
                showSnackBar("取消收藏成功!")
            })

            mCollectArticleErrorMsg.observe(this@SearchActivity, Observer {
                showSnackBar(it)
                mArticleAdapter.data[mPosition].collect = !mArticleAdapter.data[mPosition].collect
                mArticleAdapter.notifyItemChanged(mPosition + 1)
            })

            mUnCollectArticleErrorMsg.observe(this@SearchActivity, Observer {
                showSnackBar(it)
                mArticleAdapter.data[mPosition].collect = !mArticleAdapter.data[mPosition].collect
                mArticleAdapter.notifyItemChanged(mPosition + 1)
            })
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
                commonListPager.visibility = View.VISIBLE
                commonListPager.switchLoadingLayout()
                mViewModel.fetchSearchResult(0, mSearchKey!!)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (TextUtils.isEmpty(query)) {
                    commonListPager.visibility = View.INVISIBLE
                    tvHotSearchLabel.visibility = View.VISIBLE
                    hotSearchTagLayout.visibility = View.VISIBLE
                }
                return true
            }
        })

        window.enterTransition.addListener(object : TransitionUtil.TransitionListenerAdapter() {
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

    private fun setupCommonListPager() {
        commonListPager = findViewById(R.id.commonListPager)

        commonListPager.setAdapter(mArticleAdapter)

        commonListPager.commonListPagerListener = object : CommonListPager.CommonListPagerListener {
            override fun retry() {
                mViewModel.fetchSearchResult(0, mSearchKey!!)
            }

            override fun refresh() {
                mViewModel.fetchSearchResult(0, mSearchKey!!)
            }

            override fun loadMore(page: Int) {
                mViewModel.fetchSearchResult(page, mSearchKey!!)
            }
        }

        mArticleAdapter.run {
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                mPosition = position

                when (view.id) {
                    R.id.cvItemRoot -> {
                        forwardWanWebActivity(mArticleAdapter.data[position].title.fromHtml(), mArticleAdapter.data[position].link,
                            mArticleAdapter.data[position].id, mArticleAdapter.data[position].collect)
                    }
                    R.id.collectButton -> {
                        if(WanAndroid.currentUser != null) {
                            if(mArticleAdapter.data[position].collect) {
                                (view as CollectButton).startUncollectAnim()
                                mViewModel.unCollectArticle(mArticleAdapter.data[position].id)
                            }else {
                                (view as CollectButton).startCollectAnim()
                                mViewModel.collectArticle(mArticleAdapter.data[position].id)
                            }
                            mArticleAdapter.data[position].collect = !mArticleAdapter.data[position].collect
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
                    commonListPager.visibility = View.VISIBLE
                    commonListPager.showLoading()
                    mViewModel.fetchSearchResult(0, mSearchKey!!)
                    true
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(event: OnCollectChangedEvent) {
        if (event.id == mArticleAdapter.data[mPosition].id) {
            mArticleAdapter.data[mPosition].collect = event.isCollected
            mArticleAdapter.notifyItemChanged(mPosition)
            EventManager.removeStickyEvent(event)
        }
    }

}