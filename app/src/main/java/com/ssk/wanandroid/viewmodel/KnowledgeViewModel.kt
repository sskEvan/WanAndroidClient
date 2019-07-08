package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.KnowledgeTabVo
import com.ssk.wanandroid.repository.KnowledgeRepository

/**
 * Created by shisenkun on 2019-06-24.
 */
class KnowledgeViewModel : BaseViewModel() {

    private val mRepository by lazy { KnowledgeRepository() }

    val mKnowledgeTabVoList: MutableLiveData<List<KnowledgeTabVo>> = MutableLiveData()
    val mKnowledgeSubTabVoList: MutableLiveData<List<KnowledgeTabVo>> = MutableLiveData()
    val mFetchKnowledgeTabListErrorMsg: MutableLiveData<String> = MutableLiveData()

    fun fetchKnowledgeTagList() {
        launchOnUI {
            val result = mRepository.getKnowledgeTabList()
            handleResonseResult(result,
                {
                    mKnowledgeTabVoList.value = result.data

                    val knowledgeTagVoList = mutableListOf<KnowledgeTabVo>()
                    result.data?.forEach {
                        knowledgeTagVoList.add(it)
                        knowledgeTagVoList.addAll(it.children)
                    }
                    mKnowledgeSubTabVoList.value = knowledgeTagVoList
                },
                { mFetchKnowledgeTabListErrorMsg.value = result.errorMessage })
        }
    }


}