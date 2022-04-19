package com.yunianshu.library.ext

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2

/**
 * Create by WingGL
 * createTime: 2022/4/1
 */
@BindingAdapter("current")
fun ViewPager2.setCurrent(paper:Int){
    if(paper>=0){
        currentItem = paper
    }
}

@BindingAdapter("useEnable")
fun ViewPager2.useEnable(boolean: Boolean){
    this.isUserInputEnabled = boolean
}