package com.yunianshu.library.ext

import androidx.databinding.BindingAdapter
import com.yunianshu.library.view.StickerListener
import com.yunianshu.sticker.Sticker
import com.yunianshu.sticker.StickerView

/**
 * Create by WingGL
 * createTime: 2022/4/2
 */

@BindingAdapter("addSticker")
fun StickerView.addSticker(sticker: Sticker?){
    sticker?.let {
        this.addSticker(it)
    }
}

@BindingAdapter("listener")
fun StickerView.listener(listener: StickerListener){
    this.onStickerOperationListener = listener
}

@BindingAdapter("refresh")
fun StickerView.refresh(boolean: Boolean){
    if(boolean){
        invalidate()
    }
}