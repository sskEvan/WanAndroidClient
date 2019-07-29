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
    fun login(@Field("username") userName: String,
              @Field("password") passWord: String): Deferred<BaseResponse<UserVo>>

    @FormUrlEncoded
    @POST("/user/register")
    fun register(@Field("username") userName: String,
                 @Field("password") passWord: String, @Field("repassword") rePassWord: String): Deferred<BaseResponse<UserVo>>

    @GET("/banner/json")
    fun getBanner(): Deferred<BaseResponse<List<BannerVo>>>

    @GET("/article/list/{page}/json")
    fun getHomeArticleList(@Path("page") page: Int): Deferred<BaseResponse<ArticleListVo>>

    @GET("/project/tree/json")
    fun getProjectTagList(): Deferred<BaseResponse<List<ProjectTagVo>>>

    @GET("/article/list/{page}/json")
    fun getProjectDetailList(@Path("page") page: Int,
                             @Query("cid") cid: Int): Deferred<BaseResponse<ArticleListVo>>

    @GET("/tree/json")
    fun getKnowledgeTabList(): Deferred<BaseResponse<List<KnowledgeTabVo>>>

    @GET("/article/list/{page}/json")
    fun getKnowledgeDetailList(@Path("page") page: Int,
                               @Query("cid") cid: Int): Deferred<BaseResponse<ArticleListVo>>

    @GET("/hotkey/json")
    fun getHotSearch(): Deferred<BaseResponse<List<HotSearchVo>>>

    @FormUrlEncoded
    @POST("/article/query/{page}/json")
    fun searchArticle(@Path("page") page: Int,
                      @Field("k") key: String): Deferred<BaseResponse<ArticleListVo>>

    @POST("/lg/collect/{id}/json")
    fun collectArticle(@Path("id") id: Int): Deferred<BaseResponse<ArticleListVo>>

    @POST("/lg/uncollect_originId/{id}/json")
    fun unCollectArticle(@Path("id") id: Int): Deferred<BaseResponse<ArticleListVo>>

    @GET("/lg/collect/list/{page}/json")
    fun getCollectedArticleList(@Path("page") page: Int): Deferred<BaseResponse<ArticleListVo>>

    @GET("/lg/todo/v2/list/{page}/json?status=0")
    fun getTodoList(@Path("page") page: Int,
                    @Query("type") type: Int): Deferred<BaseResponse<TodoListVo>>

    @POST("/lg/todo/add/json")
    fun addSchedule(@Query("title") title: String,
        @Query("content") content: String,
        @Query("date") date: String,
        @Query("type") type: Int,
        @Query("priority") priority: Int
    ): Deferred<BaseResponse<TodoVo>>
}