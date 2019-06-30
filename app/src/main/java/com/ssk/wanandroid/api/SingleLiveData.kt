package com.ssk.wanandroid.api

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * Created by shisenkun on 2019-06-30.
 */
class SingleLiveData<T> : MutableLiveData<T>() {
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer {
            if (it != null) {
                observer.onChanged(it)
                postValue(null)
            }
        })
    }
}