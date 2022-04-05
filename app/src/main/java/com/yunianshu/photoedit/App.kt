package com.yunianshu.photoedit

import android.app.Application
import com.drake.brv.utils.BRV
import com.yunianshu.library.BR

/**
 * Create by WingGL
 * createTime: 2022/4/5
 */
class App:Application() {

    override fun onCreate() {
        super.onCreate()
        BRV.modelId = BR.vm
    }

}