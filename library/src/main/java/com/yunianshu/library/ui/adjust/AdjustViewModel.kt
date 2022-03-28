package com.yunianshu.library.ui.adjust

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.yunianshu.library.bean.AdjustBaseInfo
import com.yunianshu.library.bean.PhotoEditItem

/**
 * Create by WingGL
 * createTime: 2022/3/21
 */
class AdjustViewModel : ViewModel() {

    var url = UnPeekLiveData<String>()
    var curBaseInfo = UnPeekLiveData<AdjustBaseInfo>()
    var curAdjustText = UnPeekLiveData<String>()
    private var baseInfos = UnPeekLiveData<List<AdjustBaseInfo>>()//每个设置进度条的参数
    private val list = UnPeekLiveData<List<PhotoEditItem>>()//设置项列表
    val originBitmap = UnPeekLiveData<Bitmap>()
    val currentBitmap = UnPeekLiveData<Bitmap>()

    fun setList(data: List<PhotoEditItem>) {
        list.postValue(data)
    }

    fun getList(): UnPeekLiveData<List<PhotoEditItem>> {
        return list
    }

    fun setInfo(info:List<AdjustBaseInfo>) {
        baseInfos.postValue(info)
    }

    fun select(pos: Int) {
        curBaseInfo.postValue(baseInfos.value?.get(pos) ?: curBaseInfo.value)
    }

    fun setProgressChanged(progress:Int){
        curBaseInfo.value!!.currentProgress = progress.toFloat()
    }

    //设置调节类型
    fun selectAdjust(content: String) {
        curAdjustText.postValue(content)
    }
}