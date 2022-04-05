package com.yunianshu.library.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.binding_recyclerview.adapter.SimpleDataBindingAdapter
import com.yunianshu.library.R
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.library.databinding.ItemTextBubleBinding

class TextStickerAdapter(context: Context) :
    SimpleDataBindingAdapter<StickerInfo, ItemTextBubleBinding>(
        context,
        R.layout.item_text_buble,
        BubbleCallBack()
    ) {
    class BubbleCallBack : DiffUtil.ItemCallback<StickerInfo>() {
        override fun areItemsTheSame(oldItem: StickerInfo, newItem: StickerInfo): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: StickerInfo, newItem: StickerInfo): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindItem(
        binding: ItemTextBubleBinding,
        item: StickerInfo,
        holder: RecyclerView.ViewHolder?
    ) {
        binding.text.setImageBitmap(item.bitmap)
    }
}