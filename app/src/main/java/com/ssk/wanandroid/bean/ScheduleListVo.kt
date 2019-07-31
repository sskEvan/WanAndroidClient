package com.ssk.wanandroid.bean

import java.io.Serializable

/**
 * Created by shisenkun on 2019-07-28.
 */
data class ScheduleListVo (val curPage: Int,
                           var datas: List<ScheduleVo>,
                           val offset: Int,
                           val over: Boolean,
                           val pageCount: Int,
                           val size: Int,
                           val total: Int): Serializable