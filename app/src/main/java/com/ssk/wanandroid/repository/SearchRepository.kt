package com.ssk.wanandroid.repository

import com.ssk.wanandroid.api.WanRetrofitClient
import com.ssk.wanandroid.base.BaseRepository
import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.ArticleListVo
import com.ssk.wanandroid.bean.HotSearchVo

/**
 * Created by shisenkun on 2019-07-15.
 */
class SearchRepository : BaseRepository() {

    suspend fun getHotSearch(): BaseResponse<List<HotSearchVo>> {
        return apiCall { WanRetrofitClient.service.getHotSearch().await() }
    }

    suspend fun searchArticle(page: Int, key: String): BaseResponse<ArticleListVo> {
        return apiCall { WanRetrofitClient.service.searchArticle(page, key).await() }
    }

    suspend fun collectArticle(articleId: Int): BaseResponse<ArticleListVo> {
        return apiCall { WanRetrofitClient.service.collectArticle(articleId).await() }
    }

    suspend fun unCollectArticle(articleId: Int): BaseResponse<ArticleListVo> {
        return apiCall { WanRetrofitClient.service.unCollectArticle(articleId).await() }
    }


}