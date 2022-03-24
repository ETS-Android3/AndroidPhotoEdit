package com.yunianshu.library.util

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.*

class NV21ToBitmap(context: Context?) {
    private val rs: RenderScript
    private val yuvToRgbIntrinsic: ScriptIntrinsicYuvToRGB
    private val yuvType: Type.Builder
    private val rgbaType: Type.Builder
    fun nv21ToBitmap(nv21: ByteArray, width: Int, height: Int): Bitmap {
        yuvType.setX(nv21.size)
        val input = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT)
        input.copyFrom(nv21)
        rgbaType.setX(width).setY(height)
        val out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT)
        yuvToRgbIntrinsic.setInput(input)
        yuvToRgbIntrinsic.forEach(out)
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        out.copyTo(bmp)
        return bmp
    }

    init {
        rs = RenderScript.create(context)
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))
        yuvType = Type.Builder(rs, Element.U8(rs))
        rgbaType = Type.Builder(rs, Element.RGBA_8888(rs))
    }
}