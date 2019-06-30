package com.ssk.wanandroid.bean

import java.io.Serializable

/**
 * Created by shisenkun on 2019-06-25.
 */
data class ArticleList( val offset: Int,
                        val size: Int,
                        val total: Int,
                        val pageCount: Int,
                        val curPage: Int,
                        val over: Boolean,
                        val datas: List<Article>): Serializable