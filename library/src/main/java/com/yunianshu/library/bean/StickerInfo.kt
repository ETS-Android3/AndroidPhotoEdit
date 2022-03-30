package com.yunianshu.library.bean

import android.graphics.Bitmap

/**
 * Create by WingGL
 * createTime: 2022/3/30
 */
data class StickerInfo(var bitmap:Bitmap, var text:String = "", var select:Boolean = false)
