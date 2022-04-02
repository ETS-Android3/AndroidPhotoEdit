package com.yunianshu.library

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.yunianshu.library.bean.BubbleInfo
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.sticker.Sticker

/**
 * Create by WingGL
 * createTime: 2022/4/1
 */
class ShareViewModel : ViewModel() {
    /**
     * 图片处理缓存库
     */
    var cacheBitmaps = UnPeekLiveData<List<Bitmap>>()

    /**
     *当前显示的图片
     */
    var currentBitmap = UnPeekLiveData<List<Bitmap>>()

    /**
     * 是否在编辑中
     */
    var showEditView = UnPeekLiveData<Boolean>()

    /**
     * 气泡集合
     */
    private var stickers = UnPeekLiveData<List<Sticker>>()

    /**
     * 当前气泡
     */
    var currentSticker = UnPeekLiveData<Sticker>()

    /**
     * 当前文字气泡参数
     */
    var textStickerInfo = UnPeekLiveData<StickerInfo>()

    /**
     * 添加气泡
     */
    fun addSticker(sticker: Sticker) {
        val list = mutableListOf<Sticker>()
        stickers.value?.let { list.addAll(it) }
        list.add(sticker)
        stickers.postValue(list)
        currentSticker.postValue(sticker)
    }

    /**
     * 获取添加的气泡
     */
}