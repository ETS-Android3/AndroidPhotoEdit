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
import com.yunianshu.library.util.TextColorType
import com.yunianshu.sticker.Sticker
import com.yunianshu.sticker.TextDrawable

/**
 * Create by WingGL
 * createTime: 2022/4/1
 * description:PhotoEditActivity共享ViewModel
 */

class ShareViewModel : ViewModel() {

    /**
     * 当前编辑状态
     */
    var editState = UnPeekLiveData<Int>()

    /**
     * 图片处理缓存库
     */
    var cacheImagePaths = UnPeekLiveData<List<String>>()

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
     * 当前贴纸集合
     */
    var stickerInfos = UnPeekLiveData<List<Sticker>>()

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
     * 文字居左
     */
    var textStickerAlignLeft = UnPeekLiveData<Boolean>()

    /**
     * 文字居中
     */
    var textStickerAlignCenter = UnPeekLiveData<Boolean>()

    /**
     * 文字居右
     */
    var textStickerAlignRight = UnPeekLiveData<Boolean>()

    /**
     * 文字斜体
     */
    var textStickerItalic = UnPeekLiveData<Boolean>()

    /**
     * 文字下划线
     */
    var textStickerUnderline = UnPeekLiveData<Boolean>()

    /**
     * 文字阴影
     */
    var textStickerShadow = UnPeekLiveData<Boolean>()

    /**
     * 文字阴影颜色
     */
    var textStickerShadowColor = UnPeekLiveData<Int>()

    /**
     * 文字字体
     */
    var textStickerFont = UnPeekLiveData<FontInfo>()

    /**
     * 文字颜色类型 0-文字颜色 1-文字背景色 2-文字阴影色 3-文字下滑线颜色
     */
    var textColorType = UnPeekLiveData<TextColorType>()


    init {
        editState.value = Contant.DEFAULT
        showTextEditView.value = false
        textStickerAlpha.value = 255f
        textStickerBold.value = false
        textStickerAlign.value = 0
        textStickerItalic.value = false
        textStickerUnderline.value = false
        textStickerShadow.value = false
        textStickerShadowColor.value = Color.BLUE
        textStickerAlignLeft.value = true
        textStickerAlignCenter.value = false
        textStickerAlignRight.value = false
        textStickerFont.value =
            FontInfo(name = "默认字体", url = null, filePath = null, fontImage = null, select = true)
        textStickerInfo.value = StickerInfo(
            bitmap = com.yunianshu.library.util.ImageUtils.drawableToBitmap(
                TextDrawable.builder()
                    .beginConfig()
                    .width(100)
                    .height(50)
                    .endConfig()
                    .buildRoundRect("Hi", Color.parseColor("#82D0E7"), 5)
            ),
            bubbleInfo = BubbleInfo(
                type = Contant.STICKER_TYPE_TEXT,
                paddingLeft = 10,
                paddingBottom = 10,
                paddingRight = 10,
                paddingTop = 10
            ),
            select = true
        )
        textColorType.value = TextColorType.TEXT
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
     * 删除贴纸
     */
    fun removeSticker(sticker: Sticker) {
        val list = mutableListOf<Sticker>()
        stickers.value?.let { list.addAll(it) }
        list.remove(sticker)
        stickers.postValue(list)
    }

    /**
     * 改变文字的透明度
     */
    fun changeTextStickerAlpha() {
        textStickerAlpha.value = textStickerAlpha.value?.plus(0.1f)

    }
}