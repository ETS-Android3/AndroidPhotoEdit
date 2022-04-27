package com.yunianshu.library.bean

import com.yunianshu.sticker.Sticker

/**
 * Create by WingGL
 * createTime: 2022/4/21
 */
data class PhotoEditStep(val type: Int, var sticker: Sticker? =null, var url: String? = null)
