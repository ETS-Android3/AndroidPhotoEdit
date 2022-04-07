package com.yunianshu.library.adapter

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kunminx.binding_recyclerview.adapter.SimpleDataBindingAdapter
import com.yunianshu.library.R
import com.yunianshu.library.bean.FontInfo
import com.yunianshu.library.databinding.ItemTextFontBinding
import com.yunianshu.sticker.TextDrawable

/**
 * Create by WingGL
 * createTime: 2022/4/7
 */
class FontAdapter(context: Context):SimpleDataBindingAdapter<FontInfo,ItemTextFontBinding>(context,
    R.layout.item_text_font,
FontCallBack()) {
    class FontCallBack : DiffUtil.ItemCallback<FontInfo>() {
        override fun areItemsTheSame(oldItem: FontInfo, newItem: FontInfo): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: FontInfo, newItem: FontInfo): Boolean {
            return true
        }

    }

    override fun onBindItem(
        binding: ItemTextFontBinding,
        item: FontInfo,
        holder: RecyclerView.ViewHolder?
    ) {
        binding.item = item
        if(item.type == 0){
            binding.ivFontImage.load(TextDrawable.builder().beginConfig().textColor(Color.BLACK).endConfig().buildRect(item.name, Color.RED))
        }else{
            binding.ivFontImage.load(item.fontImage){
                crossfade(true)
            }
        }


    }

}