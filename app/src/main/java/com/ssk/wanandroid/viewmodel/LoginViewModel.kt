package com.ssk.wanandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ssk.wanandroid.base.BaseViewModel
import com.ssk.wanandroid.bean.User
import com.ssk.wanandroid.repository.LoginRepository

/**
 * Created by shisenkun on 2019-06-18.
 */
class LoginViewModel : BaseViewModel() {

    private val mRepository by lazy { LoginRepository() }

    val mLoginUser: MutableLiveData<User> = MutableLiveData()
    val mRegisterUser: MutableLiveData<User> = MutableLiveData()
    val mLoginErrorMsg: MutableLiveData<String> = MutableLiveData()
    val mRegistErrorMsg: MutableLiveData<String> = MutableLiveData()

    fun login(userName: String, passWord: String) {
        launchOnUI(
            {
                val response = mRepository.login(userName, passWord)
                handleResonseResult(
                    response,
                    { mLoginUser.value = response.data },
                    { mLoginErrorMsg.value = response.errorMessage }
                )
            },
            {})
    }


    fun regist(userName: String, passWord: String) {
        launchOnUI({
            val response = mRepository.regist(userName, passWord)
            handleResonseResult(
                response,
                { mRegisterUser.value = response.data },
                { mRegistErrorMsg.value = response.errorMessage }
            )
        },
            {})
    }


}