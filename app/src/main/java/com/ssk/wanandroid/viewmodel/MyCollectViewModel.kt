package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.ArticleListVo
import com.ssk.wanandroid.repository.MyCollectRepository

/**
 * Created by shisenkun on 2019-07-22.
 */
class MyCollectViewModel : BaseViewModel() {

    private val mRepository by lazy { MyCollectRepository() }

    val mFetchCollectArticleResult: MutableLiveData<ArticleListVo> = MutableLiveData()
    val mFetchCollectArticleErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mUnCollectArticleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mUnCollectArticleErrorMsg: MutableLiveData<String> = MutableLiveData()


    fun fetchCollectArticleList(page: Int) {
        launchOnUI {
            val result = mRepository.getCollectArticleList(page)
            result.data!!.datas.map {
                it.collect = true
            }
            handleResonseResult(result,
                { mFetchCollectArticleResult.value = result.data},
                { mFetchCollectArticleErrorMsg.value = result.errorMsg})
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