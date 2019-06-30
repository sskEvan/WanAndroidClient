package com.ssk.wanandroid.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by shisenkun on 2019-06-18.
 */
open class BaseRepository {

    suspend fun <T : Any> apiCall(call: suspend () -> BaseResponse<T>): BaseResponse<T> {
        return withContext(Dispatchers.IO) {
            call.invoke()
        }
    }

}