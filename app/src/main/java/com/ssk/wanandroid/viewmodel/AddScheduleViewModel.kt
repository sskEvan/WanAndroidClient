package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.TodoVo
import com.ssk.wanandroid.repository.AddScheduleRepository

/**
 * Created by shisenkun on 2019-07-29.
 */
class AddScheduleViewModel : BaseViewModel() {
    private val mRepository by lazy { AddScheduleRepository() }

    val mAddScheduleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mAddScheduleErrorMsg: MutableLiveData<String> = MutableLiveData()

    fun addSchedule(
        title: String,
        content: String,
        date: String,
        type: Int,
        priority: Int
    ) {
        launchOnUI {
            val result = mRepository.addSchedule(
                title,
                content,
                date,
                type,
                priority
            )
            handleResonseResult(result,
                { mAddScheduleSuccess.value = true },
                { mAddScheduleErrorMsg.value = result.errorMsg })
        }
    }

}