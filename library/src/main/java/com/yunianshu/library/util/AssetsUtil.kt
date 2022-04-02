package com.yunianshu.library.util

import android.content.Context
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder

/**
 * Create by WingGL
 * createTime: 2022/4/2
 */
object AssetsUtil {

    fun getFromAssets(context: Context,fileName: String): String {
        try {
            val inputReader = InputStreamReader(context.resources.assets.open(fileName))
            val bufReader = BufferedReader(inputReader)
            var line = ""
            var result = StringBuilder()
            bufReader.useLines {lines->
                lines.forEach {
                    result.append(it)
                }
            }
            return result.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getAssetsFile(context: Context,fileName: String):InputStream{
       return context.resources.assets.open(fileName)
    }
}