package com.yunianshu.library.ui.fillter

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.yunianshu.library.bean.FilterItem

/**
 * Create by WingGL
 * createTime: 2022/3/21
 */
class FilterViewModel:ViewModel() {

    var list = UnPeekLiveData<List<FilterItem>>()

    var currentItem = UnPeekLiveData<FilterItem>()
}