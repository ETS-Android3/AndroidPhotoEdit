package com.yunianshu.library.response

/**
 * Create by WingGL
 * createTime: 2022/4/6
 * description:字体服务器返回的数据
 */
data class FontResponse(
    var code: Int,
    var data: FontData,
    var msg: String,
    var status: Boolean
)

data class FontData(
    var count: Int,
    var list: List<Font>
)

data class Font(
    var create_time: Int,
    var create_uid: Int,
    var file: String,
    var font_id: Int,
    var font_name: String,
    var image: String,
    var sort: Int,
    var status: Int,
    var thumbnail: String,
    var types_id: Int,
    var update_time: Int
)