package com.ssk.wanandroid.base

import android.net.ParseException
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by shisenkun on 2019-06-18.
 */
open class BaseRepository {

    companion object {
        const val EXCEPTION_CODE_NOTWORK_ERROR = -9999
        const val EXCEPTION_CODE_NOTWORK_POOR = -9998
        const val EXCEPTION_CODE_PARSE_ERROR = -9997
        const val EXCEPTION_CODE_UNKOWN_ERROR = -9996

        const val EXCEPTION_MSG_NOTWORK_ERROR = "网络错误,请检测网络连接"
        const val EXCEPTION_MSG_NOTWORK_POOR = "网络不给力,请检测网络连接"
        const val EXCEPTION_MSG_PARSE_ERROR = "解析错误"
        const val EXCEPTION_MSG_UNKOWN_ERROR = "位置错误"
    }

    suspend fun <T> apiCall(call: suspend () -> BaseResponse<T>): BaseResponse<T> {
        return withContext(Dispatchers.IO) {
            try {
                call.invoke()
            }catch (e: Exception) {
                handleCallException<T>(e)
            }
        }

    }

    fun <T> handleCallException(e: Exception): BaseResponse<T> {
        var exceptionResponse: BaseResponse<T>?
        when (e) {
            is JSONException,
            is JsonParseException,
            is ParseException -> {
                exceptionResponse = BaseResponse(EXCEPTION_CODE_PARSE_ERROR, EXCEPTION_MSG_PARSE_ERROR, null)
            }
            is ConnectException,
            is UnknownHostException -> {
                exceptionResponse = BaseResponse(EXCEPTION_CODE_NOTWORK_ERROR, EXCEPTION_MSG_NOTWORK_ERROR, null)
            }
            is SocketTimeoutException -> {
                exceptionResponse = BaseResponse(EXCEPTION_CODE_NOTWORK_POOR, EXCEPTION_MSG_NOTWORK_POOR, null)
            }
            else -> {
                exceptionResponse = BaseResponse(EXCEPTION_CODE_UNKOWN_ERROR, EXCEPTION_MSG_UNKOWN_ERROR, null)
            }
        }

        return exceptionResponse
    }


}