package com.yunianshu.library

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.yunianshu.library.bean.BubbleInfo
import com.yunianshu.library.bean.FontInfo
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.library.bean.TextColorInfo
import com.yunianshu.library.util.ImageUtils
import com.yunianshu.sticker.Sticker
import com.yunianshu.sticker.TextDrawable

/**
 * Create by WingGL
 * createTime: 2022/4/1
 * description:PhotoEditActivity共享ViewModel
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
    var showTextEditView = UnPeekLiveData<Boolean>()

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

    /**
     * 文字字体
     */
    var textStickerFont = UnPeekLiveData<FontInfo>()


    init {
        showTextEditView.value = false
        textStickerAlpha.value = 255f
        textStickerBold.value = false
        textStickerAlign.value = 0
        textStickerItalic.value = false
        textStickerUnderline.value = false
        textStickerFont.value = FontInfo(name = "默认字体",url = null,filePath = null, fontImage = null, select = true)
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