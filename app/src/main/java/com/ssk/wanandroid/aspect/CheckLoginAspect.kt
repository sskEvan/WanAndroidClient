package com.ssk.wanandroid.aspect

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.ssk.wanandroid.R
import com.ssk.wanandroid.app.WanAndroid
import com.ssk.wanandroid.aspect.annotation.CheckLogin
import com.ssk.wanandroid.ext.logDebug
import com.ssk.wanandroid.ext.logError
import com.ssk.wanandroid.ext.logWarn
import com.ssk.wanandroid.ext.showToast


import com.ssk.wanandroid.view.activity.LoginActivity

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

@Aspect
class CheckLoginAspect {

    /**
     * 1、应用中用到了哪些注解，放到当前的切入点进行处理（找到需要处理的切入点）
     *  execution，以方法执行时作为切点，触发Aspect类
     *  * *(..)) 可以处理ClickBehavior这个类所有的方法
     */
    @Pointcut("execution(@com.ssk.wanandroid.aspect.annotation.CheckLogin * *(..))")
    fun methodCutPoint() {
    }

    /**
     *  2、对切入点如何处理
     */
    @Around("methodCutPoint()")
    @Throws(Throwable::class)
    fun joinPoint(joinPoint: ProceedingJoinPoint): Any? {

        val obj = joinPoint.getThis()

        if (WanAndroid.currentUser != null) {
            logWarn("检测到已登录！")
            return joinPoint.proceed()
        } else {
            logWarn("检测到未登录！")
            showToast("请先登陆!")
            val signature = joinPoint.signature as MethodSignature
            val annotation = signature.method.getAnnotation(CheckLogin::class.java) as CheckLogin
            val requestCode = annotation.requestCode

            if (obj is Activity) {
                if(requestCode == -1) {
                    obj.startActivity(Intent(obj, LoginActivity::class.java))
                }else {
                    obj.startActivityForResult(Intent(obj, LoginActivity::class.java), requestCode)
                }
                obj.overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_none)
                // 不再执行方法（切入点）
                return null
            } else if (obj is Fragment) {
                if(requestCode == -1) {
                    obj.activity!!.startActivity(Intent(obj.context, LoginActivity::class.java))
                }else {
                    obj.startActivityForResult(Intent(obj.context, LoginActivity::class.java), requestCode)
                }
                obj.activity!!.overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_none)
                // 不再执行方法（切入点）
                return null
            } else {
                return joinPoint.proceed()
            }
        }
    }


}
