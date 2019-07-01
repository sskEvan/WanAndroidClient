package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.ArticleList
import com.ssk.wanandroid.bean.Banner
import com.ssk.wanandroid.repository.HomeRepository

/**
 * Created by shisenkun on 2019-06-24.
 */
class HomeViewModel : BaseViewModel() {

    private val mRepository by lazy { HomeRepository() }

    val mBanners: MutableLiveData<List<Banner>> = MutableLiveData()
    val mArticleList: MutableLiveData<ArticleList> = MutableLiveData()
    val mArticleListErrorMsg: MutableLiveData<String> = MutableLiveData()

    fun fetchBanners() {

        launchOnUI{
                val result = mRepository.getBanners()
                handleResonseResult(result,
                    { mBanners.value = result.data})
            }
    }

    fun fetchArticleList(page: Int) {
        launchOnUI{
                val result = mRepository.getArticleList(page)
                handleResonseResult(result,
                    {
                        mArticleList.value = result.data
                    },
                    {
                        mArticleListErrorMsg.value = result.errorMessage
                    })
            }
    }

}