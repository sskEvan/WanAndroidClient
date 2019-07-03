package com.ssk.wanandroid.repository

import com.ssk.wanandroid.api.WanRetrofitClient
import com.ssk.wanandroid.base.BaseRepository
import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.ArticleListVo
import com.ssk.wanandroid.bean.BannerVo

/**
 * Created by shisenkun on 2019-06-24.
 */
class HomeRepository : BaseRepository() {

    suspend fun getBanners(): BaseResponse<List<BannerVo>> {
        return apiCall { WanRetrofitClient.service.getBanner().await() }
    }

    suspend fun getArticleList(page: Int): BaseResponse<ArticleListVo> {
        return apiCall { WanRetrofitClient.service.getHomeArticles(page).await() }
    }

}