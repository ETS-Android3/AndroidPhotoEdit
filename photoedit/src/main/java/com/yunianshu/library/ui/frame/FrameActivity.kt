package com.yunianshu.library.ui.frame

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blankj.utilcode.util.*
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.lxj.xpopup.XPopup
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.Contant
import com.yunianshu.library.R
import com.yunianshu.library.adapter.FrameAdapter
import com.yunianshu.library.response.Frame
import com.yunianshu.library.response.FrameResponse
import com.yunianshu.library.util.HttpUtil.request
import com.yunianshu.library.view.AlbumImageView
import com.yunianshu.library.view.AlbumImageView.ALBUM_IMAGE_SHAPE
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.concurrent.thread


/**
 * 4.相框调整
 */
@Deprecated("替换为UCrop实现  link-> FrameNewActivity'")
class FrameActivity : BaseActivity() {

    private lateinit var viewModel: FrameViewModel
    private lateinit var adapter: FrameAdapter
    private lateinit var url: String
    private var width: Int = 0
    private var height: Int = 0
    private lateinit var typeName: String
    private lateinit var view: AlbumImageView


    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(FrameViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = FrameAdapter(this)
        return DataBindingConfig(R.layout.activity_frame, BR.vm, viewModel)
            .addBindingParam(BR.click, FrameClickProxy())
            .addBindingParam(BR.adapter, adapter)
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
        url = intent.getStringExtra(Contant.KEY_URL).toString()
        typeName = intent.getStringExtra(Contant.KEY_TYPENAME).toString()
        width = intent.getIntExtra(Contant.KEY_WIDTH, 0)
        height = intent.getIntExtra(Contant.KEY_HEIGHT, 0)
        viewModel.url.postValue(url)
        val source = BitmapFactory.decodeFile(url)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        view = AlbumImageView(this, ALBUM_IMAGE_SHAPE, source, arrayOf(bitmap), 0F, 0F)
        val layoutParams = view.layoutParams
        view.layoutParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
        findViewById<LinearLayout>(R.id.frame_layout).addView(view)
        adapter.setOnItemClickListener { _, item, _ ->
            addFrameView(item.image)
        }
        val loading = XPopup.Builder(this@FrameActivity)
            .dismissOnBackPressed(false)
            .isLightNavigationBar(true)
            .isViewMode(true)
            .isDarkTheme(false)
            .asLoading()
        loading.show()
        GlobalScope.launch {
            try {
                getHttpData()
                runOnUiThread {
                    loading.dismiss()
                }
            } catch (e: Exception) {
                ToastUtils.showShort("获取相框资源失败")
                runOnUiThread {
                    loading.dismiss()
                }
            }
        }
    }
    //加载相框控件
    private fun addFrameView(imageUrl:String){
        val source = BitmapFactory.decodeFile(url)
        var imageurl:URL? = null;
        try {
            imageurl =URL(imageUrl)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        thread {
            try {
                val conn:HttpURLConnection = imageurl!!.openConnection() as HttpURLConnection
                conn.doInput = true
                conn.connect()
                val input:InputStream  = conn.inputStream;
                var bitmap = BitmapFactory.decodeStream(input);
                bitmap = Bitmap.createScaledBitmap(bitmap,width,height,true)
                input.close();
                if (bitmap != null) {
                    view = AlbumImageView(this@FrameActivity, ALBUM_IMAGE_SHAPE, source, arrayOf(bitmap), 0F, 0F)
                    val layoutParams = view.layoutParams
                    view.layoutParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
                    runOnUiThread {
                        findViewById<LinearLayout>(R.id.frame_layout).removeAllViews()
                        findViewById<LinearLayout>(R.id.frame_layout).addView(view)
                    }
                }
            } catch ( e: IOException) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取网络资源
     */

    private suspend fun getHttpData() {
        val list = mutableListOf<Frame>()
        val response =
            request("https://businessapi.hprtupgrade.com/api/bphoto.beautiful_photos/dataPhotoFrameList?page=1&Size=0&model=${typeName}")
        val fromJson = GsonUtils.fromJson(response, FrameResponse::class.java)
        fromJson.data.list?.forEach {
            list.add(it)
        }
        viewModel.list.postValue(list)
    }


    //获取合并图片
    fun montage(xiangkuang: Bitmap, phto: Bitmap?, phtoX: Int, phtoY: Int): Bitmap? {
        val newBitmap =
            Bitmap.createBitmap(xiangkuang.width, xiangkuang.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(phto!!, phtoX.toFloat(), phtoY.toFloat(), null)
        canvas.drawBitmap(xiangkuang, 0f, 0f, null)
        return newBitmap
    }

    inner class FrameClickProxy {

        fun cancel() {
            finish()
        }

        /**
         * 保存图片
         */
        fun complete() {
            val bitmap = view.result
            bitmap?.let {
                var path = Utils.getApp()
                    .getExternalFilesDir("edit")!!.absolutePath + File.separator + "frame_" + System.currentTimeMillis() + ".jpg"
                FileUtils.createOrExistsFile(path)
                ImageUtils.save(bitmap,path,Bitmap.CompressFormat.JPEG)
                setResult(Contant.ADJUST, intent.setData(Uri.fromFile(File(path))))
                finish()
            }

        }
    }

}