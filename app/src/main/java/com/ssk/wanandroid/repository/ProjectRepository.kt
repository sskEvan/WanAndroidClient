package com.ssk.wanandroid.repository

import com.ssk.wanandroid.api.WanRetrofitClient
import com.ssk.wanandroid.base.BaseRepository
import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.ProjectTagVo

/**
 * Created by shisenkun on 2019-06-24.
 */
class ProjectRepository : BaseRepository() {

    suspend fun getProjectTags(): BaseResponse<List<ProjectTagVo>> {
        return apiCall { WanRetrofitClient.service.getProjectTags().await() }
    }

}