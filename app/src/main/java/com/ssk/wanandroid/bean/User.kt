package com.ssk.wanandroid.bean

/**
 * Created by shisenkun on 2019-06-18.
 */
data class User(val collectIds: List<Int>,
                val email: String,
                val icon: String,
                val id: Int,
                val password: String,
                val type: Int,
                val username: String)