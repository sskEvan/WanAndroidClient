package com.ssk.wanandroid.base

import android.net.ParseException
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParseException
import kotlinx.coroutines.*
import org.json.JSONException
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by shisenkun on 2019-06-18.
 */
open class BaseViewModel : ViewModel(), LifecycleObserver {

    fun launchOnUI(actionBlock: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            coroutineScope {
               actionBlock()
            }
        }
    }

    suspend fun handleResonseResult(
        response: BaseResponse<Any>,
        successBlock: suspend CoroutineScope.() -> Unit,
        failedBlock: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            if (response.errorCode == 0) {
                successBlock()
            } else {
                failedBlock()
            }
        }
    }

    suspend fun handleResonseResult(
        response: BaseResponse<Any>,
        successBlock: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            if (response.errorCode == 0) {
                successBlock()
            }
        }
    }


}