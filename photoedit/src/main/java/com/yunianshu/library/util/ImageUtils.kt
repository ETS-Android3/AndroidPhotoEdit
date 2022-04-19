package com.yunianshu.library.util

import android.content.Context
import android.graphics.*
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.blankj.utilcode.util.FileUtils
import com.yunianshu.sticker.TextDrawable

object ImageUtils {
    /**
     * Bitmap转化为ARGB数据，再转化为NV21数据
     *
     * @param src    传入ARGB_8888的Bitmap
     * @param width  NV21图像的宽度
     * @param height NV21图像的高度
     * @return nv21数据
     */
    fun bitmapToNv21(src: Bitmap?, width: Int, height: Int): ByteArray? {
        return if (src != null && src.width >= width && src.height >= height) {
            val argb = IntArray(width * height)
            src.getPixels(argb, 0, width, 0, 0, width, height)
            argbToNv21(argb, width, height)
        } else {
            null
        }
    }

    /**
     * ARGB数据转化为NV21数据
     *
     * @param argb   argb数据
     * @param width  宽度
     * @param height 高度
     * @return nv21数据
     */
    private fun argbToNv21(argb: IntArray, width: Int, height: Int): ByteArray {
        val frameSize = width * height
        var yIndex = 0
        var uvIndex = frameSize
        var index = 0
        val nv21 = ByteArray(width * height * 3 / 2)
        for (j in 0 until height) {
            for (i in 0 until width) {
                val R = argb[index] and 0xFF0000 shr 16
                val G = argb[index] and 0x00FF00 shr 8
                val B = argb[index] and 0x0000FF
                val Y = (66 * R + 129 * G + 25 * B + 128 shr 8) + 16
                val U = (-38 * R - 74 * G + 112 * B + 128 shr 8) + 128
                val V = (112 * R - 94 * G - 18 * B + 128 shr 8) + 128
                nv21[yIndex++] = (if (Y < 0) 0 else if (Y > 255) 255 else Y).toByte()
                if (j % 2 == 0 && index % 2 == 0 && uvIndex < nv21.size - 2) {
                    nv21[uvIndex++] = (if (V < 0) 0 else if (V > 255) 255 else V).toByte()
                    nv21[uvIndex++] = (if (U < 0) 0 else if (U > 255) 255 else U).toByte()
                }
                ++index
            }
        }
        return nv21
    }

    fun hashCode(path: String): Int {
        var result = FileUtils.getFileName(path).hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + FileUtils.getFileExtension(path).hashCode()
        result = 31 * result + FileUtils.getSize(path).hashCode()
        result = 31 * result + FileUtils.getFileLastModified(path).hashCode()
        return result
    }

    /**
     * 纯色图片改变颜色
     */
    fun setSingleColorImageByARGB(
        baseBitmap: Bitmap,
        rValue: Int,
        gValue: Int,
        bValue: Int,
        alpha: Int
    ): Bitmap {

        // 1.获取一个与baseBitmap大小一致的可编辑的空图片
        var afterBitmap = Bitmap.createBitmap(
            baseBitmap.width,
            baseBitmap.height, baseBitmap.config
        )
        // 2.使用Bitmap对象创建画布Canvas, 然后创建画笔Paint。
        var canvas = Canvas(afterBitmap)
        var paint = Paint()
        // 根据SeekBar定义RGBA的矩阵, 通过修改矩阵第五列颜色的偏移量改变图片的颜色
        val src = floatArrayOf(
            0f, 0f, 0f, 0f, rValue.toFloat(),
            0f, 0f, 0f, 0f, gValue.toFloat(),
            0f, 0f, 0f, 0f, bValue.toFloat(),
            0f, 0f, 0f, 1f, 0f
        )

        // 3.定义ColorMatrix，并指定RGBA矩阵
        val colorMatrix = ColorMatrix()
        colorMatrix.set(src)
        // 4.使用ColorMatrix创建一个ColorMatrixColorFilter对象, 作为画笔的滤镜, 设置Paint的颜色
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        // 5.通过指定了RGBA矩阵的Paint把原图画到空白图片上
        canvas.drawBitmap(baseBitmap, Matrix(), paint)
        return afterBitmap
    }

    /**
     * 纯色图片改变颜色
     */
    fun setSingleColorImageByARGB(
        baseBitmap: Bitmap,
        colorStr: String
    ): Bitmap {
        val color =  Color.parseColor(colorStr)
        return setSingleColorImageByARGB(baseBitmap, color.red, color.green, color.blue, 255)
    }

    /**
     * 图片转换
     * param drawable 文字图片
     */
    fun drawableToBitmap(drawable: TextDrawable): Bitmap {
        val config =
            if (drawable.getOpacity() != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        val createBitmap =
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, config)
        val canvas = Canvas(createBitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return createBitmap
    }

}