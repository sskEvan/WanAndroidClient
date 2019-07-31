package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.ScheduleListVo
import com.ssk.wanandroid.bean.ScheduleVo
import com.ssk.wanandroid.repository.ScheduleRepository
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by shisenkun on 2019-07-28.
 */
class ScheduleViewModel : BaseViewModel() {

    private val mRepository by lazy { ScheduleRepository() }
    private val sdf = SimpleDateFormat("yyyy年MM月dd日")

    val mScheduleListVo: MutableLiveData<ScheduleListVo> = MutableLiveData()
    val mFetchScheduleListErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mCompleteScheduleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mCompleteScheduleErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mDeleteScheduleSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mDeleteScheduleErrorMsg: MutableLiveData<String> = MutableLiveData()


    fun fetchScheduleList(page: Int, type: Int, status: Int) {
        launchOnUI {
            val result = mRepository.getScheduleList(page, type, status)
            handleResonseResult(result,
                {
                    val newScheduleVoList = mutableListOf<ScheduleVo>()
                    var date = 0L
                    result.data!!.datas.forEach {
                        if (date != it.date) {
                            date = it.date
                            newScheduleVoList.add(ScheduleVo(date, sdf.format(Date(date))))
                        }
                        newScheduleVoList.add(it)
                    }
                    result.data.datas = newScheduleVoList
                    mScheduleListVo.value = result.data
                },
                { mFetchScheduleListErrorMsg.value = result.errorMsg })
        }
    }

    fun completeTodo(id: Int, status: Int) {
        launchOnUI {
            val result = mRepository.completeSchedule(id, status)
            handleResonseResult(result,
                { mCompleteScheduleSuccess.value = true },
                { mCompleteScheduleErrorMsg.value = result.errorMsg })
        }
    }

    fun deleteTodo(id: Int) {
        launchOnUI {
            val result = mRepository.deleteSchedule(id)
            handleResonseResult(result,
                { mDeleteScheduleSuccess.value = true },
                { mDeleteScheduleErrorMsg.value = result.errorMsg })
        }
    }

}