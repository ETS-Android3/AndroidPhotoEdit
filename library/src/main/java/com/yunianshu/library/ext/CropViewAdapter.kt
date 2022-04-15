package com.yunianshu.library.ext

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.databinding.BindingAdapter
import com.canhub.cropper.CropImageView
import com.yunianshu.library.bean.CropViewBase
import java.io.File

/**
 * Create by WingGL
 * createTime: 2022/3/28
 */

@BindingAdapter("setConfig")
fun CropImageView.setConfig(base: CropViewBase?) {
    base?.let {
        this.isAutoZoomEnabled = it.isAutoZoomEnabled
        this.isShowProgressBar = it.isShowProgressBar
        this.cropShape = it.cropShape
        this.scaleType = it.scaleType
        if(it.width > 0 && it.height > 0)
        this.setAspectRatio(it.width,it.height)
        this.setImageBitmap(BitmapFactory.decodeFile(it.uri.encodedPath))
    }
}
@BindingAdapter("setRotatedDegrees")
fun CropImageView.setRotatedDegrees(degrees: Int?) {
    degrees?.let {
        this.rotateImage(degrees)
    }
}
