package com.ssk.wanandroid

import android.app.Application
import android.content.ContextWrapper
import android.os.Handler
import com.google.gson.Gson
import com.ssk.wanandroid.bean.UserVo
import com.ssk.wanandroid.service.AccountManager

/**
 * Created by shisenkun on 2019-06-17.
 */

private lateinit var INSTANCE: Application

class App : Application() {

    companion object {
        val uiHandler = Handler()
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        if(AccountManager.currentUserJson.isNotEmpty()) {
            AccountManager.currentUser = Gson().fromJson(AccountManager.currentUserJson, UserVo::class.java)
        }
    }
}

object AppContext : ContextWrapper(INSTANCE)

