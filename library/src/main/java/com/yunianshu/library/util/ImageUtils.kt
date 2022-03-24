package com.yunianshu.library.util

import android.graphics.Bitmap

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
}