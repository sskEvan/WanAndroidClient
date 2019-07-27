package com.ssk.wanandroid.repository

import com.ssk.wanandroid.api.WanRetrofitClient
import com.ssk.wanandroid.base.BaseRepository
import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.ArticleListVo
import com.ssk.wanandroid.bean.HotSearchVo

/**
 * Created by shisenkun on 2019-07-22.
 */
class MyCollectRepository : BaseRepository() {

    suspend fun getCollectArticleList(page: Int): BaseResponse<ArticleListVo> {
        return apiCall { WanRetrofitClient.service.getCollectedArticleList(page).await() }
    }

    suspend fun unCollectArticle(articleId: Int): BaseResponse<ArticleListVo> {
        return apiCall { WanRetrofitClient.service.unCollectArticle(articleId).await() }
    }

}