package com.yunianshu.library

/**
 * Create by WingGL
 * createTime: 2022/3/28
 */
object Contant {

    const val DEFAULT = -1
    const val ADJUST:Int = 0
    const val FILTER:Int = 1
    const val CROP:Int = 2
    const val FRAME:Int = 3
    const val STICKER:Int = 4
    const val WORDS:Int = 5

    const val KEY_URL:String = "url"
    const val KEY_WIDTH:String = "width"
    const val KEY_HEIGHT:String = "height"
    const val KEY_ROTATE:String = "rotate"
    const val KEY_TYPENAME:String = "typeName"

    const val STICKER_BTN_HALF_SIZE:Int = 30

    //贴纸类型 0为贴纸，1为文字贴纸，2为带气泡的贴纸,3为不能改变气泡颜色的贴纸
    const val STICKER_TYPE_DEFAULT:Int = 0
    const val STICKER_TYPE_TEXT:Int = 1
    const val STICKER_TYPE_TEXT_BUBBLE:Int = 2
    const val STICKER_TYPE_TEXT_BUBBLE_COLOR_NO_CHANGE:Int = 3
}