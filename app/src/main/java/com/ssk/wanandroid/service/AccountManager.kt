package com.ssk.wanandroid.service

import com.ssk.wanandroid.bean.User
import com.ssk.wanandroid.ext.pref

/**
 * Created by shisenkun on 2019-06-17.
 */

object AccountManager {
    var username by pref("")
    var password by pref("")

    var isLogin = false
    var currentUser: User? = null
}
