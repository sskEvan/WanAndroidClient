package com.ssk.wanandroid.repository

import com.ssk.wanandroid.api.WanRetrofitClient
import com.ssk.wanandroid.base.BaseRepository
import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.UserVo

/**
 * Created by shisenkun on 2019-06-18.
 */
class LoginRepository: BaseRepository() {

    suspend fun login(userName: String, passWord: String): BaseResponse<UserVo> {
        return apiCall { WanRetrofitClient.service.login(userName, passWord).await() }
    }

    suspend fun regist(userName: String, passWord: String): BaseResponse<UserVo> {
        return apiCall { WanRetrofitClient.service.register(userName, passWord, passWord).await() }
    }

}