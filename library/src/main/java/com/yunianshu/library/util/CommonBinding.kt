package com.yunianshu.library.util

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.yunianshu.indicatorseekbar.widget.IndicatorSeekBar
import com.yunianshu.indicatorseekbar.widget.OnSeekChangeListener
import com.yunianshu.library.R
import java.io.File




/**
 * Create by WingGL
 * createTime: 2022/3/22
 */
@BindingAdapter("visiable")
fun View.visiable(show:Boolean){
    this.visibility = if(show) View.VISIBLE else View.GONE
    if(show){
        startPropertyValueHolderAnimShow(this)
    }else{
        startPropertyValueHolderAnim(this)
    }
}
fun startPropertyValueHolderAnim(v: View?) {
    val scaleXProper = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f)
    val scaleYProper = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f)
    val animator: ValueAnimator =
        ObjectAnimator.ofPropertyValuesHolder(v,scaleXProper, scaleYProper)
    animator.duration = 500
    animator.start()
}
fun startPropertyValueHolderAnimShow(v: View?) {
    val alphaProper = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
    val scaleXProper = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f)
    val scaleYProper = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f)
    val animator: ValueAnimator =
        ObjectAnimator.ofPropertyValuesHolder(v, alphaProper, scaleXProper, scaleYProper)
    animator.duration = 500
    animator.start()
}

@BindingAdapter("OnSeekChangeListener")
fun setOnSeekChangeListener(seekBar:IndicatorSeekBar,listener: OnSeekChangeListener){
    seekBar.onSeekChangeListener = listener
}

@BindingAdapter("load")
fun ImageView.load(url:String?){
    url?.let {
        var file = Uri.fromFile(File(it))
        this.load(file){
            placeholder(R.drawable.ic_load_default)
        }
    }
}

@BindingAdapter("loadBitmap")
fun ImageView.load(bitmap :Bitmap?){
    setImageBitmap(bitmap)
//    bitmap?.let {
//        this.load(bitmap){
//            placeholder(R.drawable.ic_load_default)
//        }
//    }
}

@BindingAdapter("max","min","progress")
fun IndicatorSeekBar.setMax(max:Float,min:Float,progress:Float){
    this.min = min
    this.max = max
    this.setProgress(progress)
}
