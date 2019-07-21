package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.ArticleListVo
import com.ssk.wanandroid.repository.ProjectDetailRepository

/**
 * Created by shisenkun on 2019-06-24.
 */
class ProjectDetailViewModel : BaseViewModel() {

    val mProjectDetailList: MutableLiveData<ArticleListVo> = MutableLiveData()
    val mFetchProjectDetailListErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mCollectArticleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mUnCollectArticleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mCollectArticleErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mUnCollectArticleErrorMsg: MutableLiveData<String> = MutableLiveData()

    private val mRepository by lazy { ProjectDetailRepository() }

    fun fetchProjectDetailList(page: Int, id: Int) {

        launchOnUI {
            val result = mRepository.getProjectDetailList(page, id)
            handleResonseResult(result,
                { mProjectDetailList.value = result.data },
                { mFetchProjectDetailListErrorMsg.value = result.errorMsg })
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