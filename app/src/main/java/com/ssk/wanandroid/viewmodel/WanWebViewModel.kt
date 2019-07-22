package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.repository.WanWebRepository


/**
 * Created by shisenkun on 2019-07-22.
 */
class WanWebViewModel  : BaseViewModel() {

    private val mRepository by lazy { WanWebRepository() }
    val mCollectArticleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mUnCollectArticleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mCollectArticleErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mUnCollectArticleErrorMsg: MutableLiveData<String> = MutableLiveData()


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