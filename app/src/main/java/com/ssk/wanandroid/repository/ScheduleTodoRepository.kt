package com.ssk.wanandroid.repository

import com.ssk.wanandroid.api.WanRetrofitClient
import com.ssk.wanandroid.base.BaseRepository
import com.ssk.wanandroid.base.BaseResponse
import com.ssk.wanandroid.bean.TodoListVo
import com.ssk.wanandroid.bean.TodoVo

/**
 * Created by shisenkun on 2019-07-28.
 */
class ScheduleTodoRepository : BaseRepository() {

    suspend fun getTodoList(page: Int, type: Int): BaseResponse<TodoListVo> {
        return apiCall { WanRetrofitClient.service.getTodoList(page, type).await() }
    }

    suspend fun completeTodo(id: Int, status: Int): BaseResponse<TodoVo> {
        return apiCall { WanRetrofitClient.service.completeTodo(id, status).await() }
    }

    suspend fun deleteTodo(id: Int): BaseResponse<TodoVo> {
        return apiCall { WanRetrofitClient.service.deleteTodo(id).await() }
    }

}