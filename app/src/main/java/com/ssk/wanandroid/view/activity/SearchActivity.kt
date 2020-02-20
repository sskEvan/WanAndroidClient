package com.ssk.wanandroid.view.activity

import android.app.Activity
import android.content.Intent
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
import com.ssk.lib_annotation.annotation.BindContentView
import com.ssk.wanandroid.R
import com.ssk.wanandroid.aspect.annotation.CheckLogin
import com.ssk.wanandroid.base.WanActivity
import com.ssk.wanandroid.bean.ArticleVo
import com.ssk.wanandroid.bean.HotSearchVo
import com.ssk.wanandroid.event.OnCollectChangedEvent
import com.ssk.wanandroid.ext.fromHtml
import com.ssk.wanandroid.util.AndroidVersion
import com.ssk.wanandroid.util.TransitionUtil
import com.ssk.wanandroid.view.adapter.SearchAdapter
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
@BindContentView(R.layout.activity_search)
class SearchActivity : WanActivity<SearchViewModel>() {

    companion object {
        private const val REQUEST_CODE_COLLECT = 100
    }

    private lateinit var mSearchAdapter: SearchAdapter
    private var mSearchKey: String? = null
    private lateinit var etSearch: EditText
    private lateinit var commonListPager: CommonListPager<ArticleVo>
    private var mPosition = 0
    private lateinit var mCollectButton: CollectButton

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
                mSearchAdapter.data[mPosition].collect = !mSearchAdapter.data[mPosition].collect
                mSearchAdapter.notifyItemChanged(mPosition + 1)
            })

            mUnCollectArticleErrorMsg.observe(this@SearchActivity, Observer {
                showSnackBar(it)
                mSearchAdapter.data[mPosition].collect = !mSearchAdapter.data[mPosition].collect
                mSearchAdapter.notifyItemChanged(mPosition + 1)
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

        mSearchAdapter = SearchAdapter(mutableListOf())
        commonListPager.setAdapter(mSearchAdapter)

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

        mSearchAdapter.run {
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                mPosition = position
                mCollectButton = view as CollectButton
                when (view.id) {
                    R.id.cvItemRoot -> {
                        forwardWanWebActivity(mSearchAdapter.data[position].title.fromHtml(), mSearchAdapter.data[position].link,
                            mSearchAdapter.data[position].id, mSearchAdapter.data[position].collect)
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
        if(mSearchAdapter.data[mPosition].collect) {
            mCollectButton.startUncollectAnim()
            mViewModel.unCollectArticle(mSearchAdapter.data[mPosition].id)
        }else {
            mCollectButton.startCollectAnim()
            mViewModel.collectArticle(mSearchAdapter.data[mPosition].id)
        }
        mSearchAdapter.data[mPosition].collect = !mSearchAdapter.data[mPosition].collect
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OnCollectChangedEvent) {
        if (event.id == mSearchAdapter.data[mPosition].id) {
            mSearchAdapter.data[mPosition].collect = event.isCollected
            mSearchAdapter.notifyItemChanged(mPosition)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_COLLECT) {
            handleCollectAction()
        }
    }

}