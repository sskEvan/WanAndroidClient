package com.ssk.wanandroid.app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.ssk.wanandroid.bean.UserVo
import com.ssk.wanandroid.ext.pref

/**
 * Created by shisenkun on 2019-07-21.
 */
@SuppressLint("StaticFieldLeak")
object WanAndroid {

    lateinit var uiHandler: Handler

    var username by pref("")
    var password by pref("")
    var currentUserJson by pref("")

    var currentUser: UserVo? = null

    fun init() {
        uiHandler = Handler(Looper.getMainLooper())
        checkLoginState()
    }

    fun checkLoginState() {
        if(currentUserJson.isNotEmpty()) {
            currentUser = Gson().fromJson(currentUserJson, UserVo::class.java)
        }
    }

    fun logout() {
        currentUserJson = ""
        currentUser = null
    }

    val appPackage: String
        get() = AppContext.packageName

}