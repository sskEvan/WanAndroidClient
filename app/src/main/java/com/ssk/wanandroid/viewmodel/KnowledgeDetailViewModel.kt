package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.ArticleListVo
import com.ssk.wanandroid.repository.KnowledgeDetailRepository

/**
 * Created by shisenkun on 2019-07-08.
 */
class KnowledgeDetailViewModel : BaseViewModel() {

    val mKnowledgeDetailList: MutableLiveData<ArticleListVo> = MutableLiveData()
    val mFetchKnowledgeDetailListErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mCollectArticleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mUnCollectArticleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mCollectArticleErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mUnCollectArticleErrorMsg: MutableLiveData<String> = MutableLiveData()

    private val mRepository by lazy { KnowledgeDetailRepository() }

    fun fetchKnowledgeDetailList(page: Int, id: Int) {

        launchOnUI {
            val result = mRepository.getKnowledgeDetailList(page, id)
            handleResonseResult(result,
                { mKnowledgeDetailList.value = result.data },
                { mFetchKnowledgeDetailListErrorMsg.value = result.errorMsg })
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