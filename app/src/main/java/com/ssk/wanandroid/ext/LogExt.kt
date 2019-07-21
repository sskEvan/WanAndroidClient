package com.ssk.wanandroid.ext

import android.util.Log
import com.ssk.wanandroid.app.WanAndroid

/**
 * Created by shisenkun on 2019-07-21.
 */

private const val VERBOSE = 1
private const val DEBUG = 2
private const val INFO = 3
private const val WARN = 4
private const val ERROR = 5
private const val APP_LOG_TAG = "WanAndroid"

private val level = if (WanAndroid.isDebug) VERBOSE else WARN

fun Any.logVerbose(msg: String?) {
    if (level <= VERBOSE) {
        Log.v(javaClass.simpleName, msg.toString())
    }
}

fun Any.logDebug(msg: String?) {
    if (level <= DEBUG) {
        Log.d(APP_LOG_TAG, msg.toString())
    }
}

fun Any.logInfo(msg: String?) {
    if (level <= INFO) {
        Log.i(APP_LOG_TAG, msg.toString())
    }
}

fun Any.logWarn(msg: String?, tr: Throwable? = null) {
    if (level <= WARN) {
        if (tr == null) {
            Log.w(APP_LOG_TAG, msg.toString())
        } else {
            Log.w(APP_LOG_TAG, msg.toString(), tr)
        }
    }
}

fun Any.logError(msg: String?, tr: Throwable) {
    if (level <= ERROR) {
        Log.e(APP_LOG_TAG, msg.toString(), tr)
    }
}

fun logVerbose(tag: String, msg: String?) {
    if (level <= VERBOSE) {
        Log.v(tag, msg.toString())
    }
}

fun logDebug(tag: String, msg: String?) {
    if (level <= DEBUG) {
        Log.d(tag, msg.toString())
    }
}

fun logInfo(tag: String, msg: String?) {
    if (level <= INFO) {
        Log.i(tag, msg.toString())
    }
}

fun logWarn(tag: String, msg: String?, tr: Throwable? = null) {
    if (level <= WARN) {
        if (tr == null) {
            Log.w(tag, msg.toString())
        } else {
            Log.w(tag, msg.toString(), tr)
        }
    }
}

fun logError(tag: String, msg: String?, tr: Throwable) {
    if (level <= ERROR) {
        Log.e(tag, msg.toString(), tr)
    }
}