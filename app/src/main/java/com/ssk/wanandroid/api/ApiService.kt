package com.ssk.wanandroid.api

import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.*
import kotlinx.coroutines.Deferred
import retrofit2.http.*

/**
 * Created by shisenkun on 2019-06-18.
 */
interface ApiService {

    companion object {
        const val BASE_URL = "https://www.wanandroid.com"
    }

    @FormUrlEncoded
    @POST("/user/login")
    fun login(@Field("username") userName: String, @Field("password") passWord: String): Deferred<BaseResponse<UserVo>>

    @FormUrlEncoded
    @POST("/user/register")
    fun register(@Field("username") userName: String, @Field("password") passWord: String, @Field("repassword") rePassWord: String): Deferred<BaseResponse<UserVo>>

    @GET("/banner/json")
    fun getBanner(): Deferred<BaseResponse<List<BannerVo>>>

    @GET("/article/list/{page}/json")
    fun getHomeArticleList(@Path("page") page: Int): Deferred<BaseResponse<ArticleListVo>>

    @GET("/project/tree/json")
    fun getProjectTagList(): Deferred<BaseResponse<List<ProjectTagVo>>>

    @GET("/article/list/{page}/json")
    fun getProjectDetailList(@Path("page") page: Int, @Query("cid") cid: Int): Deferred<BaseResponse<ArticleListVo>>

    @GET("/tree/json")
    fun getKnowledgeTabList(): Deferred<BaseResponse<List<KnowledgeTabVo>>>

}