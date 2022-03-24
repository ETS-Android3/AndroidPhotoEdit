package com.yunianshu.library.ui.adjust

import android.graphics.*
import android.util.Log
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.gpuapplication.ImageFilterEngine
import com.yunianshu.indicatorseekbar.widget.IndicatorSeekBar
import com.yunianshu.indicatorseekbar.widget.OnSeekChangeListener
import com.yunianshu.indicatorseekbar.widget.SeekParams
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.PhotoEditAdapter
import com.yunianshu.library.R
import com.yunianshu.library.bean.AdjustBaseInfo
import com.yunianshu.library.bean.PhotoEditItem
import com.yunianshu.library.util.ImageUtils
import java.io.ByteArrayOutputStream
import java.io.FileInputStream

/**
 * 1.调整处理界面
 */
class AdjustmentActivity : BaseActivity() {

    private var infos = mutableListOf<AdjustBaseInfo>()

    init {
        infos.add(AdjustBaseInfo(pos = 0,  max=200f, currentProgress = 0f))//亮度
        infos.add(AdjustBaseInfo(pos = 1, currentProgress = 10f))//对比度
        infos.add(AdjustBaseInfo(pos = 2,  max=200f,currentProgress = 20f))//饱和度
        infos.add(AdjustBaseInfo(pos = 3, currentProgress = 30f))//锐化
        infos.add(AdjustBaseInfo(pos = 4, currentProgress = 40f))//曝光
        infos.add(AdjustBaseInfo(pos = 5, currentProgress = 50f))//阴影
        infos.add(AdjustBaseInfo(pos = 6, currentProgress = 60f))//色温
        infos.add(AdjustBaseInfo(pos = 7, currentProgress = 70f))//暗角
    }

    private lateinit var viewModel: AdjustViewModel
    private lateinit var url: String
    private lateinit var adapter: PhotoEditAdapter
    private var mBmpW: Int = 0
    private var mBmpH: Int = 0
    private lateinit var mNV21Buf: ByteArray
    private lateinit var mPreviewNV21Buf: ByteArray


    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(AdjustViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = PhotoEditAdapter(this)
        return DataBindingConfig(
            R.layout.activity_adjustment,
            BR.vm,
            viewModel
        ).addBindingParam(BR.click, AdjustClickProxy())
            .addBindingParam(BR.listener, SeekBarListener())
            .addBindingParam(BR.adapter, adapter)
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
        url = intent.getStringExtra("url").toString()
        loadPhotoEditItems()
        load()
        viewModel.url.postValue(url)
        adapter.setOnItemClickListener { _, item, position ->
            viewModel.selectAdjust(item.text)
            viewModel.select(position)
        }
        viewModel.setInfo(infos)
        viewModel.selectAdjust(getString(R.string.text_brightness))
        viewModel.select(0)
    }

    private fun load() {
        try {
            val op = BitmapFactory.Options()
            op.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeFile(url, op)
            if (bitmap != null) {
                viewModel.originBitmap.postValue(bitmap)
                viewModel.currentBitmap.postValue(bitmap)
            }
            mBmpW = bitmap.width
            mBmpH = bitmap.height
            Log.i("test", " load() image size:" + mBmpW + "x" + mBmpH)
            val nv21Len: Int = mBmpW * mBmpH * 3 shr 1
            mNV21Buf = ImageUtils.bitmapToNv21(bitmap,mBmpW,mBmpH)!!
//            val nvIs = applicationContext.assets.open("testNV21.nv21")
//            val nvLen = nvIs.available()
//            Log.i("test", " load() nv21Len:$nv21Len nvLen:$nvLen")
//            val read = nvIs.read(mNV21Buf)
//            nvIs.close()
//            Log.i("test", " load() nv21 read byte:$read")
            mPreviewNV21Buf = ByteArray(nv21Len)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }




    private fun loadPhotoEditItems() {
        val list = mutableListOf<PhotoEditItem>()
        list.add(
            PhotoEditItem(
                R.mipmap.photoedit_icon_brightness,
                getString(R.string.text_brightness)
            )
        )
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_contrast, getString(R.string.text_contrast)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_ratio, getString(R.string.text_ratio)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_sharp, getString(R.string.text_sharp)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_exposure, getString(R.string.text_exposure)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_shadown, getString(R.string.text_shadown)))
        list.add(
            PhotoEditItem(
                R.mipmap.photoedit_icon_color_temperature,
                getString(R.string.text_color_temperature)
            )
        )
        list.add(
            PhotoEditItem(
                R.mipmap.photoedit_icon_dark_angle,
                getString(R.string.text_dark_angle)
            )
        )
        viewModel.setList(list)

    }

    inner class AdjustClickProxy {

        fun back() {
            finish()
        }
    }

    inner class SeekBarListener : OnSeekChangeListener {

        override fun onSeeking(seekParams: SeekParams) {
            changeBitmapShow(seekParams.progress)
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
            viewModel.setProgressChanged(seekBar.progress)
        }

    }

    fun changeBitmapShow(progress: Int) {
        Log.i("test", "onProgressChanged brightProgress=$progress")
        var dstBmp:Bitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_load_default)
        viewModel.curBaseInfo.value?.pos.let {
            when (it) {
                0 -> {//亮度
                    System.arraycopy(mNV21Buf, 0, mPreviewNV21Buf, 0, mNV21Buf.size)
                    ImageFilterEngine.processBrightness(
                        mPreviewNV21Buf,
                        mBmpW,
                        mBmpH,
                        progress
                    )
                    val bao = ByteArrayOutputStream()
                    val yuvImage = YuvImage(mPreviewNV21Buf, ImageFormat.NV21, mBmpW, mBmpH, null)
                    yuvImage.compressToJpeg(Rect(0, 0, mBmpW, mBmpH), 100, bao)
                    val buf = bao.toByteArray()
                    dstBmp = BitmapFactory.decodeByteArray(buf, 0, buf.size)
                }
                1 -> {//对比度
                    System.arraycopy(mNV21Buf, 0, mPreviewNV21Buf, 0, mNV21Buf.size)
                    ImageFilterEngine.processContrast(
                        mPreviewNV21Buf,
                        mBmpW,
                        mBmpH,
                        progress
                    )

                    val bao = ByteArrayOutputStream()
                    val yuvImage = YuvImage(mPreviewNV21Buf, ImageFormat.NV21, mBmpW, mBmpH, null)
                    yuvImage.compressToJpeg(Rect(0, 0, mBmpW, mBmpH), 100, bao)
                    val buf = bao.toByteArray()
                    dstBmp = BitmapFactory.decodeByteArray(buf, 0, buf.size)

                }
                2 -> {//饱和度
                    System.arraycopy(mNV21Buf, 0, mPreviewNV21Buf, 0, mNV21Buf.size)
                    ImageFilterEngine.processSaturation(mPreviewNV21Buf, mBmpW, mBmpH, progress)
                    val bao = ByteArrayOutputStream()
                    val yuvImage = YuvImage(mPreviewNV21Buf, ImageFormat.NV21, mBmpW, mBmpH, null)
                    yuvImage.compressToJpeg(Rect(0, 0, mBmpW, mBmpH), 100, bao)
                    val buf = bao.toByteArray()
                    dstBmp = BitmapFactory.decodeByteArray(buf, 0, buf.size)
                }
                3 -> {//锐化

                }
                4 -> {//曝光

                }
                5 -> {//阴影

                }
                6 -> {//色温
                    System.arraycopy(mNV21Buf, 0, mPreviewNV21Buf, 0, mNV21Buf.size)
                    ImageFilterEngine.processColorTemperature(
                        mPreviewNV21Buf,
                        mBmpW,
                        mBmpH,
                        progress
                    )
                    val bao = ByteArrayOutputStream()
                    val yuvImage = YuvImage(mPreviewNV21Buf, ImageFormat.NV21, mBmpW, mBmpH, null)
                    yuvImage.compressToJpeg(Rect(0, 0, mBmpW, mBmpH), 100, bao)
                    val buf = bao.toByteArray()
                    dstBmp = BitmapFactory.decodeByteArray(buf, 0, buf.size)
                }
                7 -> {//暗角

                }

            }
            viewModel.currentBitmap.postValue(dstBmp)
        }
    }
}