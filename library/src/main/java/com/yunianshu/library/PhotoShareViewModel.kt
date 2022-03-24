package com.yunianshu.library

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.ProtectedUnPeekLiveData
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.yunianshu.library.bean.PhotoEditItem

/**
 * Create by Winggl
 * createTime: 2022/3/21
 */
class PhotoShareViewModel:ViewModel() {


    private val list = UnPeekLiveData<List<PhotoEditItem>>()

    private val originBitmap = UnPeekLiveData<Bitmap>()

    private val currentBitmap = UnPeekLiveData<Bitmap>()

    fun setList(data:List<PhotoEditItem>){
        list.postValue(data)
    }

    fun getList():UnPeekLiveData<List<PhotoEditItem>>{
        return list
    }
}