package com.yunianshu.library.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kunminx.binding_recyclerview.adapter.SimpleDataBindingAdapter
import com.yunianshu.library.R
import com.yunianshu.library.databinding.ItemFrameBinding
import com.yunianshu.library.ext.load
import com.yunianshu.library.response.Frame

/**
 * Create by WingGL
 * createTime: 2022/4/5
 */
class FrameAdapter(context: Context) : SimpleDataBindingAdapter<Frame, ItemFrameBinding>(
    context,
    R.layout.item_frame,
    FrameCallBack()
) {
    class FrameCallBack : DiffUtil.ItemCallback<Frame>() {
        override fun areItemsTheSame(oldItem: Frame, newItem: Frame): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: Frame, newItem: Frame): Boolean {
            return true
        }

    }

    override fun onBindItem(
        binding: ItemFrameBinding,
        item: Frame,
        holder: RecyclerView.ViewHolder?
    ) {
        binding.item = item
        binding.image.load(item.src)
    }
}