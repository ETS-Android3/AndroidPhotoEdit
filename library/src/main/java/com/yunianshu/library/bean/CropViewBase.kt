package com.yunianshu.library.bean

import android.net.Uri
import com.theartofdev.edmodo.cropper.CropImageView

/**
 * Create by WingGL
 * createTime: 2022/3/28
 */
data class CropViewBase(
    var width:Int = -1,
    var height:Int = -1,
    var uri: Uri,
    var isAutoZoomEnabled: Boolean = false,
    var isShowProgressBar: Boolean = true,
    var cropShape: CropImageView.CropShape = CropImageView.CropShape.RECTANGLE,
    var scaleType: CropImageView.ScaleType = CropImageView.ScaleType.FIT_CENTER
)
