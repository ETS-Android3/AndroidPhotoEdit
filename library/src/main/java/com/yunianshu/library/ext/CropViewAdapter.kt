package com.yunianshu.library.ext

import androidx.databinding.BindingAdapter
import com.theartofdev.edmodo.cropper.CropImageView
import com.yunianshu.library.bean.CropViewBase

/**
 * Create by WingGL
 * createTime: 2022/3/28
 */

@BindingAdapter("setConfig")
fun CropImageView.setConfig(base: CropViewBase) {
    this.isAutoZoomEnabled = base.isAutoZoomEnabled
    this.isShowProgressBar = base.isShowProgressBar
    this.cropShape = base.cropShape
    this.scaleType = base.scaleType
    this.setAspectRatio(base.width,base.height)
    this.setImageUriAsync(base.uri)
}