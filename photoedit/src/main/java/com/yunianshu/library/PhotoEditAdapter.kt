package com.yunianshu.library

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kunminx.binding_recyclerview.adapter.SimpleDataBindingAdapter
import com.yunianshu.library.bean.PhotoEditItem
import com.yunianshu.library.databinding.ItemPhotoEditBinding

/**
 * Create by WingGL
 * createTime: 2022/3/21
 */
class PhotoEditAdapter(context: Context) :
    SimpleDataBindingAdapter<PhotoEditItem,ItemPhotoEditBinding>(context, R.layout.item_photo_edit, CallBack()) {

    class CallBack : DiffUtil.ItemCallback<PhotoEditItem>() {
        override fun areItemsTheSame(oldItem: PhotoEditItem, newItem: PhotoEditItem): Boolean {
           return true
        }

        override fun areContentsTheSame(oldItem: PhotoEditItem, newItem: PhotoEditItem): Boolean {
           return true
        }

    }


    override fun onBindItem(
        binding: ItemPhotoEditBinding,
        item: PhotoEditItem,
        holder: RecyclerView.ViewHolder
    ) {
        binding.item = item
        binding.itemPhotoEditIcon.load(item.res){
            placeholder(R.drawable.ic_load_default)
        }
    }


}