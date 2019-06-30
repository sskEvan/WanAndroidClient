package com.ssk.wanandroid.api

import java.lang.Exception

/**
 * Created by shisenkun on 2019-06-30.
 */
data class CallException(

    //请求tag,用于区别哪个请求出错
    val callTag:String,

    //错误
    val exception: Exception

)
