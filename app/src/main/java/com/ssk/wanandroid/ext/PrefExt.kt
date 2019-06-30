package com.ssk.wanandroid.ext

import com.ssk.wanandroid.AppContext

/**
 * Created by benny on 6/23/17.
 */
inline fun <reified R, T> R.pref(default: T) = PreferenceExt(AppContext, "",
    default, R::class.javaClass.name)