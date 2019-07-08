package com.ssk.wanandroid.repository

import com.ssk.wanandroid.api.WanRetrofitClient
import com.ssk.wanandroid.base.BaseRepository
import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.KnowledgeTabVo

/**
 * Created by shisenkun on 2019-06-24.
 */
class KnowledgeRepository : BaseRepository() {

    suspend fun getKnowledgeTabList(): BaseResponse<List<KnowledgeTabVo>> {
        return apiCall { WanRetrofitClient.service.getKnowledgeTabList().await() }
    }

}