package com.yunianshu.library.util

import android.content.Context
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2.Fetch.Impl.getInstance
import com.tonyodev.fetch2core.DownloadBlock
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object HttpUtil {

    private fun sendHttpRequest(address: String, listener: HttpCallbackListener) {
        thread {
            var connection: HttpURLConnection? = null
            try {
                val response = StringBuilder()
                val url = URL(address)
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                val input = connection.inputStream
                val reader = BufferedReader(InputStreamReader(input))
                reader.use {
                    reader.forEachLine {
                        response.append(it)
                    }
                }
                // 回调onFinish()方法
                listener.onFinish(response.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                // 回调onError()方法
                listener.onError(e)
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun sendHttpRequest(address: String,path:String, listener: HttpCallbackListener) {
        thread {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(address)
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                connection.doOutput = true
                connection.doInput = true
                connection.setRequestProperty("Content-Type", "application/octet-stream")
                connection.setRequestProperty("Connection", "Keep-Alive")
                val input = connection.inputStream
                BufferedInputStream(input).use {
                    it.copyTo(BufferedOutputStream(FileOutputStream(path)))
                }
                // 回调onFinish()方法
                listener.onFinish(path)
            } catch (e: Exception) {
                e.printStackTrace()
                // 回调onError()方法
                listener.onError(e)
            } finally {
                connection?.disconnect()
            }
        }
    }

    suspend fun request(address: String): String {
        return suspendCoroutine { continuation ->
            sendHttpRequest(address, object : HttpCallbackListener {
                override fun onFinish(response: String) {
                    continuation.resume(response)
                }

                override fun onError(e: Exception) {
                    continuation.resumeWithException(e)
                }
            })
        }
    }

    /**
     * 下载文件
     * 注意：下载字体文件会出现问题,保存的文件有些可以使用，有些不行，待查原因
     */
    suspend fun download(address: String,path: String): String {
        return suspendCoroutine { continuation ->
           sendHttpRequest(address,path, object : HttpCallbackListener {
                override fun onFinish(response: String) {
                    continuation.resume(response)
                }

                override fun onError(e: Exception) {
                    continuation.resumeWithException(e)
                }
            })
        }
    }

    private fun getFetchDownload(context: Context, address: String, path: String, listener: HttpCallbackListener) {
        val fetchConfiguration: FetchConfiguration = FetchConfiguration.Builder(context)
            .setDownloadConcurrentLimit(3)
            .build()
        var fetch = getInstance(fetchConfiguration)
        val request = Request(address, path)
        request.priority = Priority.HIGH
        request.networkType=NetworkType.ALL
        fetch.addListener(object : DownloadFetchListener() {

            override fun onCompleted(download: Download) {
                listener.onFinish(path)
                fetch.close()
            }

            override fun onError(download: Download, error: Error, throwable: Throwable?) {
                listener.onError(IOException(throwable!!.message))
            }

        })
        fetch.enqueue(request,{},{})

    }

    suspend fun fetchDownload(context: Context,address: String,path: String):String{
        return suspendCoroutine { continuation ->
            getFetchDownload(context,address,path, object : HttpCallbackListener {
                override fun onFinish(response: String) {
                    continuation.resume(response)
                }

                override fun onError(e: Exception) {
                    continuation.resumeWithException(e)
                }
            })
        }
    }

    open class DownloadFetchListener:FetchListener{

        override fun onAdded(download: Download) {
        }

        override fun onCancelled(download: Download) {
        }

        override fun onCompleted(download: Download) {
        }

        override fun onDeleted(download: Download) {
        }

        override fun onDownloadBlockUpdated(
            download: Download,
            downloadBlock: DownloadBlock,
            totalBlocks: Int
        ) {
        }

        override fun onError(download: Download, error: Error, throwable: Throwable?) {
        }

        override fun onPaused(download: Download) {
        }

        override fun onProgress(
            download: Download,
            etaInMilliSeconds: Long,
            downloadedBytesPerSecond: Long
        ) {
        }

        override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        }

        override fun onRemoved(download: Download) {
        }

        override fun onResumed(download: Download) {
        }

        override fun onStarted(
            download: Download,
            downloadBlocks: List<DownloadBlock>,
            totalBlocks: Int
        ) {
        }

        override fun onWaitingNetwork(download: Download) {
        }

    }
}