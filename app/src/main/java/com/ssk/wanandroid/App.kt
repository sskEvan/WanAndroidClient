package com.ssk.wanandroid

import android.app.Application
import android.content.ContextWrapper
import android.os.Handler

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
    }
}

object AppContext : ContextWrapper(INSTANCE)

