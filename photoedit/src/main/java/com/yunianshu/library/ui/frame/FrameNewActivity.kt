package com.yunianshu.library.ui.frame

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.RectF
import android.net.Uri
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import coil.load
import com.blankj.utilcode.util.*
import com.gyf.immersionbar.ktx.immersionBar
import com.hprt.ucrop.callback.BitmapCropCallback
import com.hprt.ucrop.view.GestureCropImageView
import com.hprt.ucrop.view.OverlayView
import com.hprt.ucrop.view.UCropView
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.lxj.xpopup.XPopup
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.Contant
import com.yunianshu.library.R
import com.yunianshu.library.adapter.FrameAdapter
import com.yunianshu.library.response.Frame
import com.yunianshu.library.response.FrameResponse
import com.yunianshu.library.util.HttpUtil
import com.yunianshu.library.util.ImageUtils
import com.yunianshu.library.view.AlbumImageView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.concurrent.thread

class FrameNewActivity : BaseActivity() {
    private lateinit var viewModel: FrameViewModel
    private lateinit var adapter: FrameAdapter
    private lateinit var url: String
    private var width: Int = 0
    private var height: Int = 0
    private lateinit var typeName: String

    private lateinit var mGestureCropImageView: GestureCropImageView
    private lateinit var mOverlayView: OverlayView
    private var aspectRatio = 0f
    private var frameUrl: String? = null

    private val frameImage: ImageView by lazy { findViewById(R.id.frame_image) }
    private val ucrop: UCropView by lazy { findViewById(R.id.ucrop) }
    private lateinit var outPath:String

    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(FrameViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = FrameAdapter(this)
        return DataBindingConfig(R.layout.activity_frame_new, BR.vm, viewModel)
            .addBindingParam(BR.click, FrameNewClickProxy())
            .addBindingParam(BR.adapter, adapter)
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
        initIntent()
        initFrameImages()
        initCropView()
    }

    private fun initIntent() {
        url = intent.getStringExtra(Contant.KEY_URL).toString()
        typeName = intent.getStringExtra(Contant.KEY_TYPENAME).toString()
        width = intent.getIntExtra(Contant.KEY_WIDTH, 0)
        height = intent.getIntExtra(Contant.KEY_HEIGHT, 0)
        if(width > ScreenUtils.getScreenWidth()){
            height = (ScreenUtils.getScreenWidth()-40)*height/width
            width = ScreenUtils.getScreenWidth()-40
        }
        viewModel.url.postValue(url)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        frameImage.setImageBitmap(bitmap)
    }

    private fun initFrameImages() {
        val loading = XPopup.Builder(this@FrameNewActivity)
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
        //选择相框
        adapter.setOnItemClickListener { _, item, _ ->
            frameUrl = item.image
            addFrameView(item.image)
        }
    }

    private fun addFrameView(imageUrl:String){
        val source = BitmapFactory.decodeFile(url)
        var imageurl: URL? = null;
        try {
            imageurl = URL(imageUrl)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        thread {
            try {
                val conn: HttpURLConnection = imageurl!!.openConnection() as HttpURLConnection
                conn.doInput = true
                conn.connect()
                val input: InputStream  = conn.inputStream;
                var bitmap = BitmapFactory.decodeStream(input);
                bitmap = ImageUtils.zoom(bitmap,width,height)
                input.close();
                if (bitmap != null) {
                    runOnUiThread {
                        frameImage.setImageBitmap(bitmap)
                    }
                }
            } catch ( e: IOException) {
                e.printStackTrace();
            }
        }

    }

    private fun initCropView() {
        aspectRatio = width.toFloat() / height
        mGestureCropImageView = ucrop.cropImageView
        mOverlayView = ucrop.overlayView
        mOverlayView.setShowCropGrid(false)
        mOverlayView.isEnabled = false
//        mOverlayView.setTargetAspectRatio(aspectRatio)
        mOverlayView.visibility = View.GONE

        mGestureCropImageView.isRotateEnabled = false;
//        mGestureCropImageView.targetAspectRatio = aspectRatio;
        mGestureCropImageView.cropToPadding = false;
        try {
            outPath = Utils.getApp()
                .getExternalFilesDir("edit")!!.absolutePath + File.separator + "frame_" + System.currentTimeMillis() + ".jpg"
            mGestureCropImageView.setImageUri(Uri.fromFile(File(url)), Uri.fromFile(File(outPath)))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val vto = frameImage.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                frameImage.viewTreeObserver.removeGlobalOnLayoutListener(this)
                val layoutParams2 = mGestureCropImageView.layoutParams as FrameLayout.LayoutParams
                layoutParams2.width = width
                layoutParams2.height = height
                layoutParams2.gravity = Gravity.CENTER
                mGestureCropImageView.layoutParams = layoutParams2
                mGestureCropImageView.setPadding(0, 0, 0, 0)
                mGestureCropImageView.setCropRect(
                    RectF(
                        0f,
                        0f,
                        layoutParams2.width.toFloat(),
                        layoutParams2.height.toFloat()
                    )
                )
            }
        })
    }

    /**
     * 获取网络资源
     */

    private suspend fun getHttpData() {
        val list = mutableListOf<Frame>()
        val response =
            HttpUtil.request("https://businessapi.hprtupgrade.com/api/bphoto.beautiful_photos/dataPhotoFrameList?page=1&Size=0&model=${typeName}")
        val fromJson = GsonUtils.fromJson(response, FrameResponse::class.java)
        fromJson.data.list.forEach {
            list.add(it)
        }
        viewModel.list.postValue(list)
    }

    inner class FrameNewClickProxy {

        fun cancel() {
            finish()
        }

        /**
         * 保存图片
         */
        fun complete() {
            if (TextUtils.isEmpty(frameUrl)) {
                ToastUtils.showShort("请选择相框")
                return
            }
            mGestureCropImageView.cropAndSaveImage(Bitmap.CompressFormat.JPEG, 100, object :
                BitmapCropCallback {
                override fun onBitmapCropped(
                    uri: Uri,
                    offsetX: Int,
                    offsetY: Int,
                    imageWidth: Int,
                    imageHeight: Int
                ) {
                    val cropBitmap = BitmapFactory.decodeFile(outPath)
                    val frame = ImageUtils.getBitmapFromDrawable(frameImage.drawable)
                    val saveImage = ImageUtils.combineImagesToSameSize(cropBitmap, frame);
                    val path = Utils.getApp()
                        .getExternalFilesDir("edit")!!.absolutePath + File.separator + "frame_" + System.currentTimeMillis() + ".jpg"
                    FileUtils.createOrExistsFile(path)
                    val save = com.blankj.utilcode.util.ImageUtils.save(
                        saveImage,
                        path,
                        Bitmap.CompressFormat.JPEG
                    )
                    if (save) {
                        setResult(Contant.FRAME, intent.setData(Uri.fromFile(File(path))))
                        finish()
                    } else {
                        ToastUtils.showShort("保存失败")
                    }
                }

                override fun onCropFailure(t: Throwable) {
                    ToastUtils.showShort("crop error")
                }
            })
        }
    }
}