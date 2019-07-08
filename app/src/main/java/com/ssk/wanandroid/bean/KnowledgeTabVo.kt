package com.ssk.wanandroid.bean

import java.io.Serializable

/**
 * Created by shisenkun on 2019-07-03.
 */
data class KnowledgeTabVo(val children: List<KnowledgeTabVo>,
                          val courseId: Int,
                          val id: Int,
                          val name: String,
                          val order: Int,
                          val parentChapterId: Int,
                          val visible: Int,
                          var selected: Boolean) : Serializable