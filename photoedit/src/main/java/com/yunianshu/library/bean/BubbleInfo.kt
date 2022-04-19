package com.yunianshu.library.bean

data class BubbleInfo(
    var ID: String = "",
    var name: String = "默认",
    var originScale: Int = 0,
    var paddingBottom: Int = 0,
    var paddingLeft: Int = 0,
    var paddingRight: Int = 0,
    var paddingTop: Int = 0,
    var path: String = "",
    var type: Int = 0/*0为贴纸，1为文字贴纸，2为带气泡的贴纸,3为不能改变气泡颜色的贴纸*/

)