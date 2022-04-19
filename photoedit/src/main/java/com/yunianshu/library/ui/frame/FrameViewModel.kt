package com.yunianshu.library.ui.frame

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.yunianshu.library.response.Frame
import com.yunianshu.library.view.AlbumImageView

/**
 * Create by WingGL
 * createTime: 2022/3/21
 */
class FrameViewModel : ViewModel() {

    val list = UnPeekLiveData<List<Frame>>()

    val url = UnPeekLiveData<String>()

    val bitmap = UnPeekLiveData<Int>()

    val frame = UnPeekLiveData<AlbumImageView>()

}