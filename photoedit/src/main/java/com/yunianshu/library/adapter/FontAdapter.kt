package com.yunianshu.library.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.kunminx.binding_recyclerview.adapter.SimpleDataBindingAdapter
import com.yunianshu.library.R
import com.yunianshu.library.bean.FontInfo
import com.yunianshu.library.databinding.ItemTextFontBinding
import com.yunianshu.library.ext.load
import com.yunianshu.library.util.ImageUtils
import com.yunianshu.sticker.TextDrawable
import kotlin.concurrent.thread

/**
 * Create by WingGL
 * createTime: 2022/4/7
 */
class FontAdapter(context: Context) : SimpleDataBindingAdapter<FontInfo, ItemTextFontBinding>(
    context,
    R.layout.item_text_font,
    FontCallBack()
) {
    class FontCallBack : DiffUtil.ItemCallback<FontInfo>() {
        override fun areItemsTheSame(oldItem: FontInfo, newItem: FontInfo): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: FontInfo, newItem: FontInfo): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindItem(
        binding: ItemTextFontBinding,
        item: FontInfo,
        holder: RecyclerView.ViewHolder?
    ) {
        binding.item = item
    }

}