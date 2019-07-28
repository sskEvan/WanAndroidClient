package com.ssk.wanandroid.util

import com.ssk.wanandroid.app.AppContext

/**
 * Created by shisenkun on 2019-06-21.
 */
/**
 * 根据手机的分辨率将dp转成为px
 */
fun dp2px(dp: Float): Int {
    val scale = AppContext.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun sp2px(sp: Float): Int {
    val scale = AppContext.resources.displayMetrics.scaledDensity
    return (sp * scale + 0.5f).toInt()
}

/**
 * 根据手机的分辨率将px转成dp
 */
fun px2dp(px: Float): Int {
    val scale = AppContext.resources.displayMetrics.density
    return (px / scale + 0.5f).toInt()
}