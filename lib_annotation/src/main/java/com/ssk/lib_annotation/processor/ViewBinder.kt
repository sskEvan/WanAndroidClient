package com.ssk.lib_annotation.processor

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssk.lib_annotation.annotation.BindContentView
import java.lang.NullPointerException

/**
 * Created by shisenkun on 2020-02-19.
 */
object ViewBinder {

    fun injectActivity(activity: Activity) {
        val clazz = activity::class.java
        val bindContentView = clazz.getAnnotation(BindContentView::class.java)
        if (bindContentView == null) {
            throw NullPointerException("BindContentView is null!")
        }

        val layoutResId = bindContentView.layoutResId

        try {
            val setContentViewMethod = clazz.getMethod("setContentView", Int::class.java)
            setContentViewMethod(activity, layoutResId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun injectFragment(fragment: Fragment, inflater: LayoutInflater, container: ViewGroup?): View {
        val clazz = fragment::class.java
        val bindContentView = clazz.getAnnotation(BindContentView::class.java)
        if (bindContentView == null) {
            throw NullPointerException("BindContentView is null!")
        }

        val layoutResId = bindContentView.layoutResId
        return inflater.inflate(layoutResId, container, false);
    }

}