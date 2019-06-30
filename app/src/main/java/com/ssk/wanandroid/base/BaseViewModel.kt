package com.ssk.wanandroid.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.lang.Exception

/**
 * Created by shisenkun on 2019-06-18.
 */
open class BaseViewModel : ViewModel(), LifecycleObserver {

    fun launchOnUI(
        actionBlock: suspend CoroutineScope.() -> Unit,
        exceptionBlock: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch {
            coroutineScope {
                try {
                    actionBlock()
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        exceptionBlock()
                    } else {
                        throw e
                    }
                }
            }
        }
    }

    fun launchOnUI(actionBlock: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            coroutineScope {
                try {
                    actionBlock()
                } catch (e: Exception) {
                    if (e is CancellationException) {
                        throw e
                    }
                }
            }
        }
    }

    suspend fun handleResonseResult(
        response: BaseResponse<Any>,
        successBlock: suspend CoroutineScope.() -> Unit,
        failedBlock: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            if (response.errorCode == -1) {
                failedBlock()
            } else {
                successBlock()
            }
        }
    }


}