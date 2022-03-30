package com.yunianshu.library.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.yunianshu.library.R
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.library.databinding.ItemPhotoStickerBinding
import com.yunianshu.library.util.load

class TextStickerAdapter(context:Context):StickerAdapter(context) {

    override fun onBindItem(
        binding: ItemPhotoStickerBinding,
        item: StickerInfo,
        holder: RecyclerView.ViewHolder
    ) {
        binding.item = item
        item.url?.let {
            binding.ivImage.load(it){
                placeholder(R.drawable.base_load_fail)
            }
        }

    }
}