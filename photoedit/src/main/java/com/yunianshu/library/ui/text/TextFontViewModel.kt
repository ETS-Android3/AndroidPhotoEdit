package com.yunianshu.library.ui.text

import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.yunianshu.library.bean.FontInfo
import com.yunianshu.library.bean.ScrollInfo

class TextFontViewModel : ViewModel() {

    var list = UnPeekLiveData<List<FontInfo>>()

    val scrollInfo = UnPeekLiveData<ScrollInfo>()
}