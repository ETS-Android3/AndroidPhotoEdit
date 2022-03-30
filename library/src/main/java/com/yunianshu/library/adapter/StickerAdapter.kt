package com.yunianshu.library.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.binding_recyclerview.adapter.SimpleDataBindingAdapter
import com.yunianshu.library.R
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.library.databinding.ItemPhotoStickerBinding
import com.yunianshu.library.util.load

/**
 * Create by WingGL
 * createTime: 2022/3/30
 */
open class StickerAdapter(context: Context) :
    SimpleDataBindingAdapter<StickerInfo, ItemPhotoStickerBinding>(
        context,
        R.layout.item_photo_sticker, StickerCallBack()
    ) {
    class StickerCallBack : DiffUtil.ItemCallback<StickerInfo>() {
        override fun areItemsTheSame(oldItem: StickerInfo, newItem: StickerInfo): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: StickerInfo, newItem: StickerInfo): Boolean {
            return true
        }

    }

    override fun onBindItem(
        binding: ItemPhotoStickerBinding,
        item: StickerInfo,
        holder: RecyclerView.ViewHolder
    ) {
        binding.item = item
        binding.ivImage.load(item.bitmap)
    }
}