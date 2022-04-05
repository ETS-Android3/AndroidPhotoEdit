package com.yunianshu.library.ext

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

/**
 * Create by WingGL
 * createTime: 2022/3/30
 */

@BindingAdapter("anim")
fun RecyclerView.anim(anim:DefaultItemAnimator){
    this.itemAnimator = anim
}