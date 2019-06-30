package com.ssk.wanandroid.repository

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.api.WARetrofitClient
import com.ssk.wanandroid.base.BaseRepository
import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.User

/**
 * Created by shisenkun on 2019-06-18.
 */
class LoginRepository: BaseRepository() {

    suspend fun login(userName: String, passWord: String): BaseResponse<User> {
        return apiCall { WARetrofitClient.service.login(userName, passWord).await() }
    }

    suspend fun regist(userName: String, passWord: String): BaseResponse<User> {
        return apiCall { WARetrofitClient.service.register(userName, passWord, passWord).await() }
    }

}