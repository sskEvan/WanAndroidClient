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

    private val mRepository by lazy { KnowledgeDetailRepository() }

    fun fetchKnowledgeDetailList(page: Int, id: Int) {

        launchOnUI {
            val result = mRepository.getKnowledgeDetailList(page, id)
            handleResonseResult(result,
                { mKnowledgeDetailList.value = result.data },
                { mFetchKnowledgeDetailListErrorMsg.value = result.errorMessage })
        }
    }

}