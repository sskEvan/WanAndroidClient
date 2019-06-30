package com.ssk.wanandroid.api

import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.ArticleList
import com.ssk.wanandroid.bean.Banner
import com.ssk.wanandroid.bean.User
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
    fun login(@Field("username") userName: String, @Field("password") passWord: String): Deferred<BaseResponse<User>>

    @FormUrlEncoded
    @POST("/user/register")
    fun register(@Field("username") userName: String, @Field("password") passWord: String, @Field("repassword") rePassWord: String): Deferred<BaseResponse<User>>

    @GET("/banner/json")
    fun getBanner(): Deferred<BaseResponse<List<Banner>>>

    @GET("/article/list/{page}/json")
    fun getHomeArticles(@Path("page") page: Int): Deferred<BaseResponse<ArticleList>>

}