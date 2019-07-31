package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.repository.EditScheduleRepository

/**
 * Created by shisenkun on 2019-07-29.
 */
class EditScheduleViewModel : BaseViewModel() {
    private val mRepository by lazy { EditScheduleRepository() }

    val mEditScheduleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mEditScheduleErrorMsg: MutableLiveData<String> = MutableLiveData()

    fun editSchedule(id: Int,
        title: String,
        content: String,
        date: String,
        type: Int,
        priority: Int
    ) {
        launchOnUI {
            val result = mRepository.editSchedule(id,
                title,
                content,
                date,
                type,
                priority
            )
            handleResonseResult(result,
                { mEditScheduleSuccess.value = true },
                { mEditScheduleErrorMsg.value = result.errorMsg })
        }
    }

}