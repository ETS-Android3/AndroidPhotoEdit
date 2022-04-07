package com.yunianshu.library.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kunminx.binding_recyclerview.adapter.SimpleDataBindingAdapter
import com.yunianshu.library.R
import com.yunianshu.library.databinding.ItemPhotoStickerBinding
import com.yunianshu.library.response.Stk

/**
 * Create by WingGL
 * createTime: 2022/3/30
 */
open class StickerAdapter(context: Context) :
    SimpleDataBindingAdapter<Stk, ItemPhotoStickerBinding>(
        context,
        R.layout.item_photo_sticker, StickerCallBack()
    ) {
    class StickerCallBack : DiffUtil.ItemCallback<Stk>() {

        override fun areItemsTheSame(oldItem: Stk, newItem: Stk): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: Stk, newItem: Stk): Boolean {
            return true
        }
    }

    override fun onBindItem(
        binding: ItemPhotoStickerBinding,
        item: Stk,
        holder: RecyclerView.ViewHolder
    ) {
        binding.ivImage.load(item.image)
    }
}