package com.ssk.wanandroid

import android.app.Application
import android.content.ContextWrapper
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import android.content.Context
import com.scwang.smartrefresh.header.BezierCircleHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.ssk.wanandroid.widget.CommonRefreshHeaderLayout


/**
 * Created by shisenkun on 2019-06-17.
 */

private lateinit var INSTANCE: Application

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

//        SmartRefreshLayout.setDefaultRefreshHeaderCreator(object : DefaultRefreshHeaderCreator {
//            override fun createRefreshHeader(context: Context, layout: RefreshLayout): RefreshHeader {
//                //layout.setPrimaryColorsId(R.color.colorPrimary, R.color.white)  //全局设置主题颜色
//                return CommonRefreshHeaderLayout(context)
//            }
//        })
    }
}

object AppContext: ContextWrapper(INSTANCE);


//private lateinit var INSTANCE: Application
//
//class App: Application() {
//    override fun onCreate() {
//        super.onCreate()
//        INSTANCE = this
//    }
//}
//
//object AppContext: ContextWrapper(INSTANCE)