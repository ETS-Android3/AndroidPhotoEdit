package com.yunianshu.library.ext

import android.widget.ViewFlipper
import androidx.databinding.BindingAdapter

/**
 * Create by WingGL
 * createTime: 2022/4/2
 */
@BindingAdapter("showNext")
fun ViewFlipper.showNext(next:Boolean){
    if(next){
        this.showNext()
    }
}

@BindingAdapter("showPrevious")
fun ViewFlipper.showPrevious(next:Boolean){
    if(!next){
        this.showPrevious()
    }
}