package com.ssk.wanandroid.repository

import com.ssk.wanandroid.api.WanRetrofitClient
import com.ssk.wanandroid.base.BaseRepository
import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.ScheduleVo

/**
 * Created by shisenkun on 2019-07-29.
 */
class EditScheduleRepository : BaseRepository() {

    suspend fun editSchedule(
        id: Int,
        title: String,
        content: String,
        date: String,
        type: Int,
        priority: Int
    ): BaseResponse<ScheduleVo> {
        return apiCall {
            WanRetrofitClient.service.editSchedule(
                id,
                title,
                content,
                date,
                type,
                priority
            ).await()
        }
    }


}