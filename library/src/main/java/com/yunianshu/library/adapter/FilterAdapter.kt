package com.yunianshu.library.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.binding_recyclerview.adapter.SimpleDataBindingAdapter
import com.yunianshu.library.R
import com.yunianshu.library.bean.FilterItem
import com.yunianshu.library.databinding.ItemPhotoFilterBinding

/**
 * Create by WingGL
 * createTime: 2022/3/29
 */
class FilterAdapter(context: Context):SimpleDataBindingAdapter<FilterItem,ItemPhotoFilterBinding>(context,
    R.layout.item_photo_filter, FilterCallBack()
) {
    class FilterCallBack : DiffUtil.ItemCallback<FilterItem>() {
        override fun areItemsTheSame(oldItem: FilterItem, newItem: FilterItem): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: FilterItem, newItem: FilterItem): Boolean {
           return true
        }

    }

    override fun onBindItem(
        binding: ItemPhotoFilterBinding,
        item: FilterItem,
        holder: RecyclerView.ViewHolder
    ) {
        binding.item = item
       if(item.select){
           binding.ivFilterImage.background = ContextCompat.getDrawable(binding.root.context, R.drawable.ic_square)
       }else{
           binding.ivFilterImage.background = null
       }
        binding.ivFilterImage.setImageBitmap(item.bitmap)
    }
}