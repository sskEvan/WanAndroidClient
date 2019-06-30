package com.ssk.wanandroid.base

/**
 * Created by shisenkun on 2019-06-18.
 */
open class BaseResponse<out T>(val errorCode: Int,
                           val errorMessage: String,
                           val data: T)