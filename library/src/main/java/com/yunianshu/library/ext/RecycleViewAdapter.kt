package com.yunianshu.library.ext

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.yunianshu.library.bean.ScrollInfo
import com.yunianshu.library.util.RecycleViewUtils

/**
 * Create by WingGL
 * createTime: 2022/3/30
 */

@BindingAdapter("anim")
fun RecyclerView.anim(anim: DefaultItemAnimator) {
    this.itemAnimator = anim
}

/**
 * 偏移步数
 */
var lastPos = 0
@BindingAdapter("scrollToPositionByStep", requireAll = false)
fun scrollToPosition(
    recyclerView: RecyclerView, info: ScrollInfo?
) {
    info?.let {
        recyclerView?.adapter?.let {
            var count = recyclerView.adapter!!.itemCount
            val scrollToPosition: Int = if (info.pos - lastPos > 0) { //右边
                if (info.pos + info.step < recyclerView.adapter!!.itemCount) { //保证在数据长度内
                    info.pos + info.step
                } else {
                    recyclerView.adapter!!.itemCount
                }
            } else { //左边
                if (info.pos - info.step > 0) { //保证不会越界
                    info.pos - info.step
                } else {
                    0
                }
            } //要滑动的位置
            recyclerView.smoothScrollToPosition(scrollToPosition) //滑动方法
        }
    }

}