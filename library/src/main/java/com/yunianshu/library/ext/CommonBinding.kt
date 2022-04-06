package com.yunianshu.library.ext

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import coil.load
import com.bumptech.glide.Glide
import com.yunianshu.indicatorseekbar.widget.IndicatorSeekBar
import com.yunianshu.indicatorseekbar.widget.OnSeekChangeListener
import com.yunianshu.library.R
import com.yunianshu.sticker.TextDrawable
import jp.co.cyberagent.android.gpuimage.GPUImageView
import java.io.File


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
@BindingAdapter("url")
fun ImageView.url(url: String?) {
    url?.let {
        this.load(url) {
            placeholder(R.drawable.ic_load_default)
        }
    }
}
@BindingAdapter("loadResource")
fun ImageView.loadB(bitmap :Int) {
    bitmap?.let {
//        setImageResource(bitmap)
        Glide.with(this).load(it).into(this)
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
