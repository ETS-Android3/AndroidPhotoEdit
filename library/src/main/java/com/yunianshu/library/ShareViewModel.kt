package com.yunianshu.library

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.yunianshu.library.bean.BubbleInfo
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.library.bean.TextColorInfo
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
     * 文字alpha
     */
    var textStickerAlpha = UnPeekLiveData<Float>()

    /**
     * 文字粗体
     */
    var textStickerBold = UnPeekLiveData<Boolean>()

    /**
     * 文字颜色
     */
    var textStickerColor = UnPeekLiveData<TextColorInfo>()

    /**
     * 文字align
     */

    var textStickerAlign = UnPeekLiveData<Int>()

    /**
     * 文字斜体
     */
    var textStickerItalic = UnPeekLiveData<Boolean>()

    /**
     * 文字下划线
     */
    var textStickerUnderline = UnPeekLiveData<Boolean>()


    init {
        showEditView.value = false
        textStickerAlpha.value = 255f
        textStickerBold.value = false
        textStickerAlign.value = 0
        textStickerItalic.value = false
        textStickerUnderline.value = false
    }

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
     * 改变文字的透明度
     */
    fun changeTextStickerAlpha() {
        textStickerAlpha.value = textStickerAlpha.value?.plus(0.1f)

    }
}