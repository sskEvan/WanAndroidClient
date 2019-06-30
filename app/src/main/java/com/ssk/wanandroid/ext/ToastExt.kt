package com.ssk.wanandroid.ext

import android.annotation.SuppressLint
import android.os.Looper
import android.widget.Toast
import com.ssk.wanandroid.AppContext

/**
 * Created by shisenkun on 2019-06-17.
 */
private var toast: Toast? = null

/**
 * 弹出Toast信息。如果不是在主线程中调用此方法，Toast信息将会不显示。
 *
 * @param content
 * Toast中显示的内容
 */
@SuppressLint("ShowToast")
@JvmOverloads
fun showToast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (toast == null) {
            toast = Toast.makeText(AppContext, content, duration)
        } else {
            toast?.setText(content)
        }
        toast?.show()
    }
}
