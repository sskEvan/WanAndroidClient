package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.ArticleListVo
import com.ssk.wanandroid.bean.HotSearchVo
import com.ssk.wanandroid.repository.SearchRepository

/**
 * Created by shisenkun on 2019-07-15.
 */
class SearchViewModel : BaseViewModel() {

    private val mRepository by lazy { SearchRepository() }
    val mHotSearchVoList: MutableLiveData<List<HotSearchVo>> = MutableLiveData()
    val mSearchResult: MutableLiveData<ArticleListVo> = MutableLiveData()
    val mSearchResultErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mCollectArticleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mUnCollectArticleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mCollectArticleErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mUnCollectArticleErrorMsg: MutableLiveData<String> = MutableLiveData()

    fun fetchHotSearch() {
        launchOnUI{
            val result = mRepository.getHotSearch()
            handleResonseResult(result,
                { mHotSearchVoList.value = result.data})
        }
    }

    fun fetchSearchResult(page: Int, key: String) {
        launchOnUI{
            val result = mRepository.searchArticle(page, key)
            handleResonseResult(result,
                {mSearchResult.value = result.data},
                {mSearchResultErrorMsg.value = result.errorMsg})

        }
    }

    fun collectArticle(articleId: Int) {
        launchOnUI {
            val result = mRepository.collectArticle(articleId)
            handleResonseResult(result,
                { mCollectArticleSuccess.value = true},
                { mCollectArticleErrorMsg.value = "收藏失败:" + result.errorMsg})
        }
    }

    fun unCollectArticle(articleId: Int) {
        launchOnUI {
            val result = mRepository.unCollectArticle(articleId)
            handleResonseResult(result,
                { mUnCollectArticleSuccess.value = true},
                { mUnCollectArticleErrorMsg.value = "取消收藏失败:" + result.errorMsg})
        }
    }

}