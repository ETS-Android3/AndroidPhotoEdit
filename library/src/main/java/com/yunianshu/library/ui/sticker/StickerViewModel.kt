package com.yunianshu.library.ui.sticker

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.library.response.Stk
import com.yunianshu.library.view.StickerItem

/**
 * Create by WingGL
 * createTime: 2022/3/21
 */
class StickerViewModel:ViewModel() {

    var list = UnPeekLiveData<List<Stk>>()

    var addStickers = UnPeekLiveData<List<StickerItem>>()

    var bitmap = UnPeekLiveData<Bitmap>()
}