package com.ssk.wanandroid.repository

import com.ssk.wanandroid.api.WanRetrofitClient
import com.ssk.wanandroid.base.BaseRepository
import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.ScheduleListVo
import com.ssk.wanandroid.bean.ScheduleVo

/**
 * Created by shisenkun on 2019-07-28.
 */
class ScheduleRepository : BaseRepository() {

    suspend fun getScheduleList(page: Int, type: Int, status: Int): BaseResponse<ScheduleListVo> {
        return apiCall { WanRetrofitClient.service.getScheduleList(page, type, status).await() }
    }

    suspend fun completeSchedule(id: Int, status: Int): BaseResponse<ScheduleVo> {
        return apiCall { WanRetrofitClient.service.completeSchedule(id, status).await() }
    }

    suspend fun deleteSchedule(id: Int): BaseResponse<ScheduleVo> {
        return apiCall { WanRetrofitClient.service.deleteSchedule(id).await() }
    }

}