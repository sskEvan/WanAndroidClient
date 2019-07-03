package com.ssk.wanandroid.repository

import com.ssk.wanandroid.api.WanRetrofitClient
import com.ssk.wanandroid.base.BaseRepository
import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.ArticleListVo

/**
 * Created by shisenkun on 2019-06-24.
 */
class ProjectDetailRepository : BaseRepository() {

    suspend fun getProjectDetailList(page: Int, id: Int): BaseResponse<ArticleListVo> {
        return apiCall { WanRetrofitClient.service.getProjectDetailList(page, id).await() }
    }

}