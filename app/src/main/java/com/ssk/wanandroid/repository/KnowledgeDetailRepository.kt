package com.ssk.wanandroid.repository

import com.ssk.wanandroid.api.WanRetrofitClient
import com.ssk.wanandroid.base.BaseRepository
import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.ArticleListVo

/**
 * Created by shisenkun on 2019-07-08.
 */
class KnowledgeDetailRepository : BaseRepository() {

    suspend fun getKnowledgeDetailList(page: Int, id: Int): BaseResponse<ArticleListVo> {
        return apiCall { WanRetrofitClient.service.getKnowledgeDetailList(page, id).await() }
    }

}