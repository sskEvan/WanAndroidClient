package com.ssk.wanandroid.ext

import android.content.Context
import com.ssk.wanandroid.AppContext
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by shisenkun on 2019-06-17.
 */
class PreferenceExt<T>(val context: Context, val name: String, val default: T, val prefName: String = "default")
    : ReadWriteProperty<Any?, T> {

    private val prefs by lazy {
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(findProperName(property))
    }

    private fun findProperName(property: KProperty<*>) = if(name.isEmpty()) property.name else name

    private fun findPreference(key: String): T{
        return when(default){
            is Long -> prefs.getLong(key, default)
            is Int -> prefs.getInt(key, default)
            is Boolean -> prefs.getBoolean(key, default)
            is String -> prefs.getString(key, default)
            else -> throw IllegalArgumentException("Unsupported type.")
        } as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(findProperName(property), value)
    }

    private fun putPreference(key: String, value: T){
        with(prefs.edit()){
            when(value){
                is Long -> putLong(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                else -> throw IllegalArgumentException("Unsupported type.")
            }
        }.apply()
    }
}