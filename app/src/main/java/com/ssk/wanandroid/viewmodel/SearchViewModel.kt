package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.ArticleListVo
import com.ssk.wanandroid.bean.BannerVo
import com.ssk.wanandroid.bean.HotSearchVo
import com.ssk.wanandroid.repository.HomeRepository
import com.ssk.wanandroid.repository.SearchRepository

/**
 * Created by shisenkun on 2019-07-15.
 */
class SearchViewModel : BaseViewModel() {

    private val mRepository by lazy { SearchRepository() }
    val mHotSearchVoList: MutableLiveData<List<HotSearchVo>> = MutableLiveData()
    val mSearchResult: MutableLiveData<ArticleListVo> = MutableLiveData()
    val mSearchResultErrorMsg: MutableLiveData<String> = MutableLiveData()


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
                {mSearchResultErrorMsg.value = result.errorMessage})

        }
    }


}