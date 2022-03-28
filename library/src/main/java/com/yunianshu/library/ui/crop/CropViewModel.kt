package com.yunianshu.library.ui.crop

import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.yunianshu.library.bean.CropViewBase

/**
 * Create by WingGL
 * createTime: 2022/3/21
 */
class CropViewModel:ViewModel() {

    var cropViewBase = UnPeekLiveData<CropViewBase>()
}