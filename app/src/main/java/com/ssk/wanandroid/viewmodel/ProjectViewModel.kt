package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.ProjectTagVo
import com.ssk.wanandroid.repository.ProjectRepository

/**
 * Created by shisenkun on 2019-06-24.
 */
class ProjectViewModel : BaseViewModel() {

    private val mRepository by lazy { ProjectRepository() }

    val mProjectTagVoList: MutableLiveData<List<ProjectTagVo>> = MutableLiveData()
    val mFetchProjectTagListErrorMsg: MutableLiveData<String> = MutableLiveData()

    fun fetchProjectTagList() {

        launchOnUI {
            val result = mRepository.getProjectTagList()
            handleResonseResult(result,
                { mProjectTagVoList.value = result.data },
                { mFetchProjectTagListErrorMsg.value = result.errorMessage })
        }
    }


}