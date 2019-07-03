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

    private val mRepository by lazy { ProjectDetailRepository() }

    fun fetchProjectDetailList(page: Int, id: Int) {

        launchOnUI {
            val result = mRepository.getProjectDetailList(page, id)
            handleResonseResult(result,
                { mProjectDetailList.value = result.data },
                { mFetchProjectDetailListErrorMsg.value = result.errorMessage })
        }
    }


}