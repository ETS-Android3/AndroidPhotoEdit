package com.yunianshu.library.util

import androidx.recyclerview.widget.RecyclerView

object RecycleViewUtils {

    private var mFirstVisiblePosition:Int = 0 //上次点击的位置
    open fun toPosition(recyclerView: RecyclerView, i:Int, size:Int,step:Int = 0) { // i 当前点击的条目，size数据总长度
        val scrollToPosition: Int = if (i - mFirstVisiblePosition > 0) { //右边
            if (i + step < size) { //保证在数据长度内
                i + step
            } else {
                size
            }
        } else { //左边
            if (i - step > 0) { //保证不会越界
                i - step
            } else {
                0
            }
        } //要滑动的位置
        recyclerView.smoothScrollToPosition(scrollToPosition) //滑动方法
        mFirstVisiblePosition = i //重新赋值
    }
}