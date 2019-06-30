package com.ssk.wanandroid.repository

import com.ssk.wanandroid.api.WARetrofitClient
import com.ssk.wanandroid.base.BaseRepository
import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.ArticleList
import com.ssk.wanandroid.bean.Banner

/**
 * Created by shisenkun on 2019-06-24.
 */
class HomeRepository : BaseRepository() {

    suspend fun getBanners(): BaseResponse<List<Banner>> {
        return apiCall { WARetrofitClient.service.getBanner().await() }
    }

    suspend fun getArticleList(page: Int): BaseResponse<ArticleList> {
        return apiCall { WARetrofitClient.service.getHomeArticles(page).await() }
    }

}