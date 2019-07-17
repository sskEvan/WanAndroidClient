package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.ArticleListVo
import com.ssk.wanandroid.bean.BannerVo
import com.ssk.wanandroid.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by shisenkun on 2019-06-24.
 */
class HomeViewModel : BaseViewModel() {

    private val mRepository by lazy { HomeRepository() }

    val mBannerVoList: MutableLiveData<List<BannerVo>> = MutableLiveData()
    val mArticalListVo: MutableLiveData<ArticleListVo> = MutableLiveData()
    val mFetchArticleListErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mCollectArticleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mUnCollectArticleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mCollectArticleErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mUnCollectArticleErrorMsg: MutableLiveData<String> = MutableLiveData()

    fun fetchBannerList() {

        launchOnUI{
                val result = mRepository.getBannerList()
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

    fun collectArticle(articleId: Int) {
        launchOnUI {
            val result = mRepository.collectArticle(articleId)
            handleResonseResult(result,
                { mCollectArticleSuccess.value = true},
                { mCollectArticleErrorMsg.value = "收藏失败:" + result.errorMessage})
        }
    }

    fun unCollectArticle(articleId: Int) {
        launchOnUI {
            val result = mRepository.unCollectArticle(articleId)
            handleResonseResult(result,
                { mUnCollectArticleSuccess.value = true},
                { mUnCollectArticleErrorMsg.value = "取消收藏失败:" + result.errorMessage})
        }
    }

}