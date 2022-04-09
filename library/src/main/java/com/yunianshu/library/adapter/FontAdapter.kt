package com.yunianshu.library.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.blankj.utilcode.util.ConvertUtils
import com.kunminx.binding_recyclerview.adapter.SimpleDataBindingAdapter
import com.yunianshu.library.R
import com.yunianshu.library.bean.FontInfo
import com.yunianshu.library.databinding.ItemTextFontBinding
import com.yunianshu.library.util.ImageUtils
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
            var drawable:Drawable = TextDrawable.builder().beginConfig().fontSize(ConvertUtils.sp2px(20f)).align(Paint.Align.LEFT).height(40).width(100).textColor(Color.BLACK).endConfig().buildRect("默认", Color.TRANSPARENT)
//            val color = Color.parseColor("#82D0E7")
//            var bitmap = ImageUtils.setSingleColorImageByARGB(ImageUtils.drawableToBitmap(drawable as TextDrawable),color.red,color.green,color.blue,color.alpha)
//            if(item.select){
//                drawable = BitmapDrawable(bitmap)
//            }
            binding.ivFontImage.load(drawable)
        }else{
            binding.ivFontImage.load(item.fontImage){
                crossfade(true)
            }
        }


    }

}