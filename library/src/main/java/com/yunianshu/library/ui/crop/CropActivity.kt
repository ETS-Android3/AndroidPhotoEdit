package com.yunianshu.library.ui.crop

import android.graphics.*
import android.net.Uri
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.Utils
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.theartofdev.edmodo.cropper.CropImageView
import com.yunianshu.indicatorseekbar.widget.IndicatorSeekBar
import com.yunianshu.indicatorseekbar.widget.OnSeekChangeListener
import com.yunianshu.indicatorseekbar.widget.SeekParams
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.Contant
import com.yunianshu.library.R
import com.yunianshu.library.bean.CropViewBase
import java.io.File


/**
 * 3.裁剪界面
 */
class CropActivity : BaseActivity(), CropImageView.OnCropImageCompleteListener {

    private lateinit var viewModel: CropViewModel
    private val cropImageView: CropImageView by lazy { findViewById(R.id.cropImageView) }
    private var width: Int = 0
    private var height: Int = 0
    private var rotate: Boolean = false
    private lateinit var url: String

    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(CropViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_crop, BR.vm, viewModel)
            .addBindingParam(BR.click, CropClickProxy())
            .addBindingParam(BR.listener, CropSeekBarListener())
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
        url = intent.getStringExtra(Contant.KEY_URL).toString()
        width = intent.getIntExtra(Contant.KEY_WIDTH, -1)
        height = intent.getIntExtra(Contant.KEY_HEIGHT, -1)
        rotate = intent.getBooleanExtra(Contant.KEY_ROTATE, false)
        val bitmap = BitmapFactory.decodeFile(url)
//        val layoutParams = cropImageView.layoutParams as ConstraintLayout.LayoutParams
//        layoutParams.dimensionRatio = "${bitmap.width}:${bitmap.height}"
        if (bitmap == null) {
            Toast.makeText(this, getString(R.string.text_image_no_find), Toast.LENGTH_LONG).show()
            return
        }
        initCropView()
    }

    private fun initCropView() {
        cropImageView.setOnCropImageCompleteListener(this)
        cropImageView.isAutoZoomEnabled = true
        viewModel.cropViewBase.postValue(CropViewBase(width, height, Uri.parse(url)))
    }

    private fun convertBitmap(srcBitmap: Bitmap): Bitmap? {
        val width = srcBitmap.width
        val height = srcBitmap.height
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas()
        val matrix = Matrix()
        matrix.postScale(-1f, 1f)
        val newBitmap2 = Bitmap.createBitmap(srcBitmap, 0, 0, width, height, matrix, true)
        canvas.drawBitmap(
            newBitmap2,
            Rect(0, 0, width, height),
            Rect(0, 0, width, height), null
        )
        return newBitmap2
    }

    inner class CropClickProxy {

        fun back() {
            finish()
        }

        fun cancel() {
            finish()
        }

        fun complete() {
            var path = Utils.getApp()
                .getExternalFilesDir("edit")!!.absolutePath + File.separator + "crop_" + System.currentTimeMillis() + ".jpg"
            FileUtils.createOrExistsFile(path)
            cropImageView.saveCroppedImageAsync(
                Uri.fromFile(
                    File(
                        path
                    )
                )
            )
        }

        /**
         * 镜像
         */
        fun image() {
            cropImageView.isFlippedHorizontally =
                !cropImageView.isFlippedHorizontally
        }

        fun rotate90() {
            var degrees = cropImageView.rotatedDegrees
            cropImageView.rotatedDegrees = degrees + 90
        }
    }

    inner class CropSeekBarListener : OnSeekChangeListener {

        override fun onSeeking(seekParams: SeekParams) {
            cropImageView.rotatedDegrees =
                180 + seekParams.progress
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {

        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {

        }

    }

    override fun onCropImageComplete(view: CropImageView?, result: CropImageView.CropResult?) {
        result?.let {
            setResult(Contant.CROP, intent.setData(result.uri))
            finish()
        }
    }
}