package com.yunianshu.library.util

interface HttpCallbackListener {
    fun onFinish(response: String)
    fun onError(e: Exception)
}