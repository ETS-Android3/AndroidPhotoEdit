package com.yunianshu.library.util

import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener3
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

    private fun sendHttpRequest(address: String, path: String, listener: HttpCallbackListener) {
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
                val fileOutputStream = FileOutputStream(path)
                val out = BufferedOutputStream(fileOutputStream)
                BufferedInputStream(input).use {
                    it.copyTo(out)
                }
                fileOutputStream.flush()
                fileOutputStream.close()
                input.close()
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
    suspend fun download(address: String, path: String): String {
        return suspendCoroutine { continuation ->
            sendHttpRequest(address, path, object : HttpCallbackListener {
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
     */
    suspend fun downloadByOKDownload(address: String, path: String, filename: String): String {
        return suspendCoroutine { continuation ->
            try {
                val task: DownloadTask = DownloadTask.Builder(address, File(path))
                    .setFilename(filename)
                    .setMinIntervalMillisCallbackProcess(30) // 下载进度回调的间隔时间（毫秒）
                    .setPassIfAlreadyCompleted(false) // 任务过去已完成是否要重新下载
                    .setPriority(10)
                    .build()
                task.enqueue(object : OkDownloadListener() {
                    override fun completed(task: DownloadTask) {
                        task.file?.let {
                            continuation.resume(it.absolutePath)
                        }
                    }

                    override fun error(task: DownloadTask, e: java.lang.Exception) {
                        continuation.resumeWithException(e)
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                continuation.resumeWithException(e)
            }
        }
    }

    open class OkDownloadListener : DownloadListener3() {
        override fun retry(task: DownloadTask, cause: ResumeFailedCause) {
        }

        override fun connected(
            task: DownloadTask,
            blockCount: Int,
            currentOffset: Long,
            totalLength: Long
        ) {
        }

        override fun progress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
        }

        override fun started(task: DownloadTask) {
        }

        override fun completed(task: DownloadTask) {
        }

        override fun canceled(task: DownloadTask) {
        }

        override fun error(task: DownloadTask, e: java.lang.Exception) {
        }

        override fun warn(task: DownloadTask) {
        }

    }
}