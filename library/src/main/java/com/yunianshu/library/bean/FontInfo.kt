package com.yunianshu.library.bean

/**
 * Created by yunianshu on 2022/4/7.
 * @param type:0-默认，1-本地字体，2-网络字体
 */
data class FontInfo(var name: String?, var url: String?, var filePath: String?,var fontImage:String?,var type:Int = 0,var select:Boolean = false)
