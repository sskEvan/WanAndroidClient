package com.ssk.wanandroid.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by shisenkun on 2019-06-18.
 */
class NetWorkUtils {

    companion object {
        @Suppress("DEPRECATION")
        fun isNetworkAvailable(context: Context): Boolean {
            val manager = context.applicationContext.getSystemService(
                Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = manager.activeNetworkInfo
            return !(null == info || !info.isAvailable)
        }
    }
}