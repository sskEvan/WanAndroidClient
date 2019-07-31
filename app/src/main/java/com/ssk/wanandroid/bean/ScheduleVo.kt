package com.ssk.wanandroid.bean

import java.io.Serializable

/**
 * Created by shisenkun on 2019-07-28.
 */
data class ScheduleVo(
    var completeDate: Long,
    var completeDateStr: String,
    var content: String,
    var date: Long,
    var dateStr: String,
    var id: Int,
    var status: Int,
    var priority: Int,
    var title: String,
    var type: Int,
    var userId: Int
) : Serializable {

    constructor(data: Long, dateStr: String) : this(
        0,
        "",
        "",
        data,
        dateStr,
        0,
        0,
        0
        , "",
        0,
        0
    )
}