package com.yunianshu.library.ui.frame

import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData

/**
 * Create by WingGL
 * createTime: 2022/3/21
 */
class FrameViewModel:ViewModel() {

    val list = UnPeekLiveData<List<String>>()

}