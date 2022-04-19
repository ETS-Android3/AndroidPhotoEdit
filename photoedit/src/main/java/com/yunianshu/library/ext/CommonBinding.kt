package com.yunianshu.library.ext

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.databinding.BindingAdapter
import coil.load
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.yunianshu.indicatorseekbar.widget.IndicatorSeekBar
import com.yunianshu.indicatorseekbar.widget.OnSeekChangeListener
import com.yunianshu.library.R
import com.yunianshu.library.bean.FontInfo
import com.yunianshu.library.util.ImageUtils
import com.yunianshu.sticker.TextDrawable
import jp.co.cyberagent.android.gpuimage.GPUImageView
import java.io.File
import kotlin.concurrent.thread


/**
 * Create by WingGL
 * createTime: 2022/3/22
 */
@BindingAdapter("visiable")
fun View.visiable(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("OnSeekChangeListener")
fun setOnSeekChangeListener(seekBar: IndicatorSeekBar, listener: OnSeekChangeListener) {
    seekBar.onSeekChangeListener = listener
}

@BindingAdapter("load")
fun ImageView.load(url: String?) {
    url?.let {
        var file = Uri.fromFile(File(it))
        this.load(file) {
            placeholder(R.drawable.ic_load_default)
        }
    }
}

@BindingAdapter("loadFontImage")
fun ImageView.loadFontImage(item: FontInfo?) {
    item?.let {
        val color = Color.parseColor("#82D0E7")
        if (item.type == 0) {
            var drawable: Drawable =
                TextDrawable.builder().beginConfig().useFont(
                    null
                ).align(Paint.Align.LEFT).height(50).fontSize(ConvertUtils.sp2px(16f)).width(100).textColor(
                    Color.BLACK).endConfig()
                    .buildRect("默认", Color.TRANSPARENT)
            if (item.select) {
                this.load(
                    ImageUtils.setSingleColorImageByARGB(
                    ImageUtils.drawableToBitmap(drawable as TextDrawable),
                    color.red,
                    color.green,
                    color.blue,
                    color.alpha
                ))
            }else{
                this.load(drawable)
            }
        } else {
            thread {
                var bitmap = Glide.with(this)
                    .asBitmap()
                    .load(item.fontImage)
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get()
                if (item.select) {
                    bitmap = ImageUtils.setSingleColorImageByARGB(
                        bitmap,
                        color.red,
                        color.green,
                        color.blue,
                        color.alpha
                    )
                }
                this.load(bitmap) {
                    crossfade(true)
                }
            }


        }
    }
}


@BindingAdapter("url")
fun ImageView.url(url: String?) {
    url?.let {
        this.load(url) {
            placeholder(R.drawable.ic_load_default)
        }
    }
}


@BindingAdapter("loadBitmap")
fun ImageView.load(bitmap: Bitmap?) {
    setImageBitmap(bitmap)
}

@BindingAdapter("loadBitmap")
fun GPUImageView.load(bitmap: Bitmap?) {
    setImage(bitmap)
}

@BindingAdapter("max", "min", "progress")
fun IndicatorSeekBar.setMax(max: Float, min: Float, progress: Float) {
    this.min = min
    this.max = max
    this.setProgress(progress)
}

@BindingAdapter("textdrawable")
fun View.setDrawable(drawable: TextDrawable) {
    drawable?.let {
        this.background = it
    }
}

@BindingAdapter("onTouchListener")
fun View.onTouchListener(listener: View.OnTouchListener) {
    this.setOnTouchListener(listener)
}

@BindingAdapter("addView")
fun addChildView(view: LinearLayout,child: View) {
    child?.let {
        view.addView(it)
    }

}
