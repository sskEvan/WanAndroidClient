package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.TodoListVo
import com.ssk.wanandroid.bean.TodoVo
import com.ssk.wanandroid.repository.ScheduleTodoRepository
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by shisenkun on 2019-07-28.
 */
class ScheduleTodoViewModel : BaseViewModel() {

    private val mRepository by lazy { ScheduleTodoRepository() }
    private val sdf = SimpleDateFormat("yyyy年MM月dd日")

    val mTodoListVo: MutableLiveData<TodoListVo> = MutableLiveData()
    val mFetchTodoListErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mCompleteTodoSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mCompleteTodoErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mDeleteTodoSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val mDeleteTodoErrorMsg: MutableLiveData<String> = MutableLiveData()


    fun fetchTodoList(page: Int, type: Int) {
        launchOnUI {
            val result = mRepository.getTodoList(page, type)
            handleResonseResult(result,
                {
                    val newTodoVoList = mutableListOf<TodoVo>()
                    var date = 0L
                    result.data!!.datas.forEach {
                        if (date != it.date) {
                            date = it.date
                            newTodoVoList.add(TodoVo(date, sdf.format(Date(date))))
                        }
                        newTodoVoList.add(it)
                    }
                    result.data.datas = newTodoVoList
                    mTodoListVo.value = result.data
                },
                { mFetchTodoListErrorMsg.value = result.errorMsg })
        }
    }

    fun completeTodo(id: Int, status: Int) {
        launchOnUI {
            val result = mRepository.completeTodo(id, status)
            handleResonseResult(result,
                { mCompleteTodoSuccess.value = true },
                { mCompleteTodoErrorMsg.value = result.errorMsg })
        }
    }

    fun deleteTodo(id: Int) {
        launchOnUI {
            val result = mRepository.deleteTodo(id)
            handleResonseResult(result,
                { mDeleteTodoSuccess.value = true },
                { mDeleteTodoErrorMsg.value = result.errorMsg })
        }
    }

}