package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.ArticleListVo
import com.ssk.wanandroid.bean.BannerVo
import com.ssk.wanandroid.repository.HomeRepository

/**
 * Created by shisenkun on 2019-06-24.
 */
class HomeViewModel : BaseViewModel() {

    private val mRepository by lazy { HomeRepository() }

    val mBannerVoList: MutableLiveData<List<BannerVo>> = MutableLiveData()
    val mArticalListVo: MutableLiveData<ArticleListVo> = MutableLiveData()
    val mFetchArticleListErrorMsg: MutableLiveData<String> = MutableLiveData()

    fun fetchBannerList() {

        launchOnUI{
                val result = mRepository.getBanners()
                handleResonseResult(result,
                    { mBannerVoList.value = result.data})
            }
    }

    fun fetchArticleList(page: Int) {
        launchOnUI{
                val result = mRepository.getArticleList(page)
                handleResonseResult(result,
                    {
                        mArticalListVo.value = result.data
                    },
                    {
                        mFetchArticleListErrorMsg.value = result.errorMessage
                    })
            }
    }

}