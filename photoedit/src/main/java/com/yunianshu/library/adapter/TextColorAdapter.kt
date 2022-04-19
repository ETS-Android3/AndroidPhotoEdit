package com.yunianshu.library.adapter

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.binding_recyclerview.adapter.SimpleDataBindingAdapter
import com.yunianshu.library.R
import com.yunianshu.library.bean.TextColorInfo
import com.yunianshu.library.databinding.ItemTextColorBinding
import com.yunianshu.sticker.TextDrawable

/**
 * Create by WingGL
 * createTime: 2022/4/4
 */
class TextColorAdapter(context: Context) :
    SimpleDataBindingAdapter<TextColorInfo, ItemTextColorBinding>(
        context,
        R.layout.item_text_color,
        TextColorCallback()
    ) {
    class TextColorCallback : DiffUtil.ItemCallback<TextColorInfo>() {
        override fun areItemsTheSame(oldItem: TextColorInfo, newItem: TextColorInfo): Boolean {
            return oldItem.colorString == newItem.colorString
        }

        override fun areContentsTheSame(oldItem: TextColorInfo, newItem: TextColorInfo): Boolean {
            return true
        }

    }

    override fun onBindItem(
        binding: ItemTextColorBinding?,
        item: TextColorInfo?,
        holder: RecyclerView.ViewHolder?
    ) {
        if (item!!.color == -1) {
            if(item.colorString == ""){
                binding!!.drawable = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(2)
                    .textColor(Color.BLACK)
                    .endConfig()
                    .buildRound("选", Color.WHITE)
            }else{
                binding!!.drawable = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(2)
                    .textColor(Color.parseColor(item.colorString))
                    .endConfig()
                    .buildRound("选", Color.WHITE)
            }
        } else {
            binding?.drawable =
                item?.color?.let {
                    TextDrawable.builder()
                        .buildRound("", it)
                }

        }
    }
}