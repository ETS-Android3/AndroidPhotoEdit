package com.yunianshu.library.response

data class StickerResponse(
    val code: Int,
    val data: StickerData,
    val msg: String,
    val status: Boolean
)

data class StickerData(
    val count: Int,
    val list: List<Stk>
)

data class Stk(
    val create_time: Int,
    val create_uid: Int,
    val image: String,
    val sort: Int,
    val status: Int,
    val sticker_id: Int,
    val sticker_name: String,
    val thumbnail: String,
    val type_name: String,
    val types_id: Int,
    val update_time: Int,
    var select:Boolean
)