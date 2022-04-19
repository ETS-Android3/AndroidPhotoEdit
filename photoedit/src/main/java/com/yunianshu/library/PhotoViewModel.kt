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
class PhotoViewModel:ViewModel() {


    private val list = UnPeekLiveData<List<PhotoEditItem>>()

    val currentPaper = UnPeekLiveData<Int>()

    val currentText = UnPeekLiveData<String>()

    val refreshStickerView = UnPeekLiveData<Boolean>()


    fun setList(data:List<PhotoEditItem>){
        list.postValue(data)
    }

    fun getList():UnPeekLiveData<List<PhotoEditItem>>{
        return list
    }

    fun refreshStickerView(){
        refreshStickerView.postValue(true)
    }
}