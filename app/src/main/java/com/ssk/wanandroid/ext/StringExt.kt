package com.ssk.wanandroid.ext

import android.text.Html
import com.ssk.wanandroid.util.AndroidVersion

/**
 * Created by shisenkun on 2019-07-21.
 */

@Suppress("DEPRECATION")
fun String.fromHtml(): String {

    if (AndroidVersion.hasNougat()) {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
        return Html.fromHtml(this).toString()
    }

}