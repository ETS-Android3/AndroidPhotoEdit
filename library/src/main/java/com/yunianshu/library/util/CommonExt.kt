package com.yunianshu.library.util

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * Create by WingGL
 * createTime: 2022/4/2
 */

inline fun <reified T> String.toBean(dateFormat: String = "yyyy-MM-dd HH:mm:ss", lenient: Boolean = false): T = GsonBuilder().setDateFormat(dateFormat)
    .apply {
        if(lenient) setLenient()
    }.create()
    .fromJson<T>(this, object : TypeToken<T>() {}.type)