package com.ssk.wanandroid.app

import android.app.Application
import android.content.ContextWrapper

/**
 * Created by shisenkun on 2019-06-17.
 */

private lateinit var INSTANCE: Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        WanAndroid.init()
    }
}

object AppContext : ContextWrapper(INSTANCE)

