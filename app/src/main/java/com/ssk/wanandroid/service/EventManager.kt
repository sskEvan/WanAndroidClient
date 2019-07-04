package com.ssk.wanandroid.service

import org.greenrobot.eventbus.EventBus
import java.lang.Exception

/**
 * Created by shisenkun on 2019-07-04.
 */
object EventManager {

    /**
     * 注册事件管理器
     */
    fun register(obj: Any) {
        if (!EventBus.getDefault().isRegistered(obj)) {
            try {
                EventBus.getDefault().register(obj)
            }catch (e: Exception) {

            }
        }
    }

    /**
     * 发布事件
     */
    fun post(event: Any) {
        EventBus.getDefault().post(event)
    }

    /**
     * 发布粘性事件
     */
    fun postSticky(event: Any) {
        EventBus.getDefault().postSticky(event)
    }


    /**
     * 发布粘性事件
     */
    fun removeStickyEvent(event: Any) {
        EventBus.getDefault().removeStickyEvent(event)
    }

    /**
     * 反注册事件管理器
     */
    fun unregister(obj: Any) {
        if (EventBus.getDefault().isRegistered(obj)) {
            EventBus.getDefault().unregister(obj)
        }
    }

    /**
     * 取消事件传递
     * @param event
     */
    fun cancelEventDelivery(event: Any) {
        EventBus.getDefault().cancelEventDelivery(event)
    }


}
